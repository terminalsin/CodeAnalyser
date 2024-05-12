package com.codeanalyser.shop.code;

import com.codeanalyser.shop.code.dto.CodeModalDto;
import com.codeanalyser.shop.code.dto.CodeModalStatusDto;
import com.codeanalyser.shop.gpt.GptServiceDelegate;
import com.codeanalyser.shop.gpt.serializer.CodeModalJsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CodeEvaluatorService {
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private GptServiceDelegate serviceDelegate;

    @Autowired
    private CodeStorageService codeStorageServiceDelegate;

    @Autowired
    private CodeModalJsonParser codeModalJsonParser;

    @Async
    public void evaluateCode(final UUID uuid, final String username, final String code) {
        final String prompt = """
                DO NOT RESPOND WITH ANY PLAIN TEXT. Only respond with a JSON object. Do not add any comments.
                
                Given the Java code snippet, Please provide a breakdown by analyzing its various components. Try
                to find vulnerabilities (ERROR), inefficiencies (WARNING), or any other suggestions (INFO) that can
                be made to improve the code.
                                
                Finally, provide an overall rating of the code on a scale from 1 to 100 based on best practices, efficiency, and clarity.
                
                The output should be EXCLUSIVELY in a json format of the following. DO NOT RESPOND WITH ANY PLAIN TEXT:
                {
                    "name": "<name of the code snippet>",
                    "review": [
                        {
                            "code":"<ORIGINAL INPUT code copied here identically, CANNOT be multiple lines>",
                            "highlight":"<recommendation for the code, if any, for the code. Can be empty if NONE type is specified>",
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
        sendStatusToUser(username, new CodeModalStatusDto(uuid, "processing:start"));
        final String output = serviceDelegate.callOpenAIService(
                prompt.replace("{input_code}", code),
                e -> sendStatusToUser(username, new CodeModalStatusDto(uuid, e))
        );
        sendStatusToUser(username, new CodeModalStatusDto(uuid, "processing:completed"));
        // Parse the output and return the CodeModal object
        System.out.println(output);
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
    }

    public void sendStatusToUser(final String username, final CodeModalStatusDto codeModalId) {
        System.out.println("Sending status to user " + username + " with id " + codeModalId.getId());
        template.convertAndSendToUser(username, "/queue/review/status", codeModalId);
    }

    public void sendReviewToUser(final String username, CodeModalDto codeModal) {
        System.out.println("Sending status to user " + username + " with id " + codeModal.getId());
        template.convertAndSendToUser(username, "/queue/review", codeModal);
    }
}
