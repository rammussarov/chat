package io.test.chat.listener;

import io.test.chat.enums.MessageType;
import io.test.chat.model.Message;
import io.test.chat.service.UsernameStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;

class WebSocketEventListenerTest {

    private static final String USERNAME = "testuser";
    private static final String CHAT_TOPIC = "/topic/publicChat";
    private static final String USER_LIST_TOPIC = "/topic/userList";

    @Mock
    private SimpMessageSendingOperations messagingTemplate;

    @Mock
    private SessionDisconnectEvent event;

    private WebSocketEventListener eventListener;

    private UsernameStorage storage = new UsernameStorage();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        eventListener = new WebSocketEventListener(messagingTemplate, storage);
    }

    @Test
    void handleWebSocketDisconnectListener() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("username", USERNAME);
        Map<String, Object> headers = new HashMap<>();
        headers.put(SESSION_ATTRIBUTES, attributes);

        when(event.getMessage()).thenReturn(MessageBuilder.createMessage(new byte[1], new MessageHeaders(headers)));

        storage.addNewUser(USERNAME);
        final Message message = Message.builder().type(MessageType.LEAVE).sender(USERNAME).build();

        eventListener.handleWebSocketDisconnectListener(event);

        assertEquals(0, storage.getUsernames().size());
        verify(messagingTemplate, times(1)).convertAndSend(eq(USER_LIST_TOPIC), any(Set.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq(CHAT_TOPIC), eq(message));
    }
}