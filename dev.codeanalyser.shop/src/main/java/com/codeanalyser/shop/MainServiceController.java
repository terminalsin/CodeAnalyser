package com.codeanalyser.shop;

import com.codeanalyser.shop.code.CodeEvaluatorService;
import com.codeanalyser.shop.code.dto.CodeModalDto;
import com.codeanalyser.shop.code.dto.CodeModalStatusDto;
import com.codeanalyser.shop.code.CodeStorageService;
import com.codeanalyser.shop.gpt.GptServiceDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
public class MainServiceController {

    @Autowired
    private GptServiceDelegate serviceDelegate;

    @Autowired
    private CodeStorageService codeStorageServiceDelegate;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private CodeEvaluatorService codeEvaluatorService;

    @GetMapping("/my-data")
    public List<CodeModalDto> getMyData(@AuthenticationPrincipal UserDetails userDetails) {
        return codeStorageServiceDelegate.getAllCodeModals(userDetails.getUsername());
    }

    @PostMapping("/generate")
    public CodeModalStatusDto generateData(@RequestParam String code, @AuthenticationPrincipal UserDetails userDetails) {
        final String username = userDetails.getUsername();

        final UUID id = UUID.randomUUID();
        sendStatusToUser(username, new CodeModalStatusDto(id, "connecting:start"));
        codeEvaluatorService.evaluateCode(id, username, code);
        sendStatusToUser(username, new CodeModalStatusDto(id, "connecting:completed"));
        return new CodeModalStatusDto();
    }

    public void sendStatusToUser(final String username, final CodeModalStatusDto codeModalId) {
        System.out.println("Sending status to user " + username + " with id " + codeModalId.getId());
        template.convertAndSendToUser(username, "/queue/review/status", codeModalId);
    }

    @GetMapping("/use-agents")
    public String useAgents() {
        return serviceDelegate.callOpenAIService("Hello, I am a bot. How can I help you today?", e -> {});
    }
}
