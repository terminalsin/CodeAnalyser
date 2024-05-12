package com.codeanalyser.shop.apilog;

import com.codeanalyser.shop.code.dto.CodeModalStatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class SubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SubscribeListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        // Check if the original event is a subscription to the /queue/review/status destination
        System.out.println("Subscribed to: " + event.getMessage().getHeaders().get("simpDestination"));
        if (!event.getMessage().getHeaders().get("simpDestination").equals("/user/queue/review/status")) {
            return;
        }
        messagingTemplate.convertAndSendToUser(event.getUser().getName(),
                "/queue/review/status", new CodeModalStatusDto(null, "connected"));
    }
}