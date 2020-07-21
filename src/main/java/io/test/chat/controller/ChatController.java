package io.test.chat.controller;

import io.test.chat.model.Message;
import io.test.chat.service.UsernameStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

/**
 * Controller for handling chat events.
 */

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final UsernameStorage storage;

    @MessageMapping("/chat/sendMessage")
    @SendTo("/topic/publicChat")
    public Message sendMessage(@Payload Message message) {
        return message;
    }

    @MessageMapping("/chat/auth")
    public void authUser(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        final String sender = message.getSender();
        headerAccessor.getSessionAttributes().put("username", sender);
        storage.addNewUser(sender);

        messagingTemplate.convertAndSend("/topic/publicChat", message);
        messagingTemplate.convertAndSend("/topic/userList", storage.getUsernames());
    }
}
