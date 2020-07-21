package io.test.chat.controller;

import io.test.chat.service.UsernameStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MainControllerTest {

    private static final String USERNAME = "test";
    private static final String CHAT_TOPIC = "/topic/publicChat";
    private static final String USER_LIST_TOPIC = "/topic/userList";

    @Mock
    private SimpMessageSendingOperations messagingTemplate;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    private MainController mainController;

    private UsernameStorage storage = new UsernameStorage();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mainController = new MainController(messagingTemplate, storage);
    }

    @Test
    void testShowMainPage() {
        when(session.getAttribute("username")).thenReturn(USERNAME);
        when(request.getSession()).thenReturn(session);

        mainController.showMainPage(request, model);

        verify(messagingTemplate, times(1)).convertAndSend(eq(USER_LIST_TOPIC), any(Set.class));

        assertEquals(1, storage.getUsernames().size());
        assertTrue(storage.isExist(USERNAME));
    }

    @Test
    void testLogin() {
        when(session.getAttribute("username")).thenReturn(USERNAME);
        when(request.getSession()).thenReturn(session);

        mainController.login(request, USERNAME, model);

        verify(messagingTemplate, times(1)).convertAndSend(eq(USER_LIST_TOPIC), any(Set.class));

        assertEquals(1, storage.getUsernames().size());
        assertTrue(storage.isExist(USERNAME));
    }

    @Test
    void testLogout() {
        storage.addNewUser(USERNAME);

        when(session.getAttribute("username")).thenReturn(USERNAME);
        when(request.getSession()).thenReturn(session);

        mainController.logout(request);
        assertEquals(0, storage.getUsernames().size());
    }
}