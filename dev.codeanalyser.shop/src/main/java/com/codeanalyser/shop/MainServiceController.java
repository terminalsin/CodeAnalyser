package com.codeanalyser.shop;

import com.codeanalyser.shop.code.CodeModalDto;
import com.codeanalyser.shop.code.CodeModalStatusDto;
import com.codeanalyser.shop.code.CodeStorageServiceDelegate;
import com.codeanalyser.shop.gpt.GptServiceDelegate;
import com.codeanalyser.shop.gpt.serializer.CodeModalJsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@RestController
public class MainServiceController {

    @Autowired
    private GptServiceDelegate serviceDelegate;

    @Autowired
    private CodeStorageServiceDelegate codeStorageServiceDelegate;

    @Autowired
    private CodeModalJsonParser codeModalJsonParser;

    @Autowired
    private SimpMessagingTemplate template;

    @GetMapping("/my-data")
    public List<CodeModalDto> getMyData(@AuthenticationPrincipal UserDetails userDetails) {
        return codeStorageServiceDelegate.getAllCodeModals(userDetails.getUsername());
    }

    @PostMapping("/generate")
    public CodeModalStatusDto generateData(@RequestParam String code, @AuthenticationPrincipal UserDetails userDetails) {
        final String username = userDetails.getUsername();

        final UUID id = UUID.randomUUID();
        sendStatusToUser(username, new CodeModalStatusDto(id, "connecting:start"));
        CompletableFuture.runAsync(() -> asyncDelegateCodeModal(id, username, code));

        return new CodeModalStatusDto();
    }

    public void sendStatusToUser(final String username, final CodeModalStatusDto codeModalId) {
        System.out.println("Sending status to user " + username + " with id " + codeModalId.getId());
        template.convertAndSendToUser(username, "/queue/review/status", codeModalId);
    }

    @SubscribeMapping("/queue/review/status")
    public CodeModalStatusDto subscribeToStatus() {
        System.out.println("Subscribed to status");
        return new CodeModalStatusDto(null, "connected");
    }

    public void sendReviewToUser(final String username, CodeModalDto codeModal) {
        System.out.println("Sending status to user " + username + " with id " + codeModal.getId());
        template.convertAndSendToUser(username, "/queue/review", codeModal);
    }

    @Async
    public void asyncDelegateCodeModal(final UUID uuid, final String username, final String code) {
        final String prompt = """
                Given the Java code snippet, Please provide a breakdown by analyzing its various components:
                1. For annotations and their usage.
                2. For relationships between entities like @OneToMany, @ManyToOne.
                3. For field declarations and their significance.
                4. For enum definitions and their roles.
                                
                For each component, specify:
                - A guessed file name for the code snippet.
                - A brief description.
                - Any suggestions for improvement or changes.
                - Highlight key areas that might need attention or are particularly well implemented.
                                
                Finally, provide an overall rating of the code on a scale from 1 to 10 based on best practices, efficiency, and clarity.
                
                The output should be EXCLUSIVELY in a json format of the following. DO NOT RESPOND WITH ANY PLAIN TEXT:
                {
                    "name": "<name of the code snippet>",
                    "review": [
                        {
                            "code":"<input code copied here, can be multiple lines>",
                            "highlight":"<recommendation, if any, for the code. Can be empty if NONE type is specified>",
                            "type":"<Type of notice. Can be 'ERROR', 'WARNING', 'INFO', 'NONE'>",
                        }
                        // do this until you've returned the entire code back in this format
                    ],
                    "score": <overall score of the code as an integer from 0-100>
                }
                
                Snippet:
                
                ```
                {input_code}
                ```
                """;
        sendStatusToUser(username, new CodeModalStatusDto(uuid, "connecting:completed"));
        sendStatusToUser(username, new CodeModalStatusDto(uuid, "processing:start"));
        final String output = serviceDelegate.callOpenAIService(
                prompt.replace("{input_code}", code),
                e -> sendStatusToUser(username, new CodeModalStatusDto(uuid, e))
        );
        sendStatusToUser(username, new CodeModalStatusDto(uuid, "processing:completed"));
        // Parse the output and return the CodeModal object
        System.out.println(output);

        try {
            // Parse the json output and set the results
            CodeModalDto parsedCodeModal = codeModalJsonParser.parseJsonToCodeModal(output);
            parsedCodeModal.setId(uuid);
            parsedCodeModal.setCode(code);
            parsedCodeModal.setOwnerUsername(username);

            // Save the parsed code modal to the database
            try {
                sendStatusToUser(username, new CodeModalStatusDto(uuid, "storage:start"));
                parsedCodeModal = codeStorageServiceDelegate.saveCodeModal(parsedCodeModal);
                sendStatusToUser(username, new CodeModalStatusDto(uuid, "storage:completed"));
            } catch (Exception e) {
                sendStatusToUser(username, new CodeModalStatusDto(uuid, "storage:failed"));
                e.printStackTrace();
            }

            sendStatusToUser(username, new CodeModalStatusDto(uuid, "completed"));
            sendReviewToUser(username, parsedCodeModal);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
        }
    }

    @GetMapping("/use-agents")
    public String useAgents() {
        return serviceDelegate.callOpenAIService("Hello, I am a bot. How can I help you today?", e -> {});
    }
}
