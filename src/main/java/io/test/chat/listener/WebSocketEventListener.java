package io.test.chat.listener;

import io.test.chat.enums.MessageType;
import io.test.chat.model.Message;
import io.test.chat.service.UsernameStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Listener for handling websocket events.
 */

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final UsernameStorage storage;

    /**
     * Sends message with type LEAVE when user session of WebSocket client is closed.
     *
     * @param event - event raised when the session of a WebSocket client using a Simple Messaging
     * Protocol as the WebSocket sub-protocol is closed.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            Message message = new Message();
            message.setType(MessageType.LEAVE);
            message.setSender(username);

            storage.removeUser(username);

            messagingTemplate.convertAndSend("/topic/publicChat", message);
            messagingTemplate.convertAndSend("/topic/userList", storage.getUsernames());
        }
    }
}
