package io.test.chat.controller;

import io.test.chat.enums.MessageType;
import io.test.chat.model.Message;
import io.test.chat.service.UsernameStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    private static final String SENDER = "sender";
    private static final String MESSAGE = "content";
    private static final String CHAT_TOPIC = "/topic/publicChat";
    private static final String USER_LIST_TOPIC = "/topic/userList";

    @Mock
    private SimpMessageSendingOperations messagingTemplate;

    @Mock
    private SimpMessageHeaderAccessor simpMessageHeaderAccessor;

    private UsernameStorage storage = new UsernameStorage();

    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        chatController = new ChatController(messagingTemplate, storage);
    }

    @Test
    void authUserTest() {
        doNothing().when(messagingTemplate).convertAndSend(any(String.class), any(Object.class));

        final Message message = Message.builder().type(MessageType.CHAT).sender(SENDER).content(MESSAGE).build();

        chatController.authUser(message, simpMessageHeaderAccessor);

        verify(messagingTemplate, times(1)).convertAndSend(eq(CHAT_TOPIC), eq(message));
        verify(messagingTemplate, times(1)).convertAndSend(eq(USER_LIST_TOPIC), any(Set.class));

        assertEquals(1, storage.getUsernames().size());
        assertTrue(storage.isExist(SENDER));
    }
}