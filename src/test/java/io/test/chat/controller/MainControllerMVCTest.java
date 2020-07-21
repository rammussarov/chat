package io.test.chat.controller;

import io.test.chat.service.UsernameStorage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import javax.servlet.http.HttpSession;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MainController.class)
class MainControllerMVCTest {

    private static final String USERNAME = "testuser";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SimpMessageSendingOperations messagingTemplate;

    @MockBean
    private UsernameStorage storage;

    @Test
    @SneakyThrows
    void testShowMainPageWithUsername() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("username")).thenReturn(USERNAME);

        final RequestPostProcessor requestPostProcessor = request -> {
            request.setSession(session);
            return request;
        };

        mockMvc.perform(get("/").with(requestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attribute("username", equalTo(USERNAME)));
    }

    @Test
    @SneakyThrows
    void testShowMainPageWithoutUsername() {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }

    @Test
    @SneakyThrows
    void testShowLoginPage() {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @SneakyThrows
    void testLoginAuthenticated() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("username")).thenReturn(USERNAME);

        final RequestPostProcessor requestPostProcessor = request -> {
            request.setSession(session);
            return request;
        };

        mockMvc.perform(post("/login").with(requestPostProcessor).param("username", USERNAME))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }


    @Test
    @SneakyThrows
    void testShowMainPageWithExistingUsername() {
        when(storage.isExist(USERNAME)).thenReturn(true);

        mockMvc.perform(post("/login").param("username", USERNAME))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", equalTo("Username already exists in a system!")));
    }

    @Test
    @SneakyThrows
    void testLoginNotAuthenticated() {

        mockMvc.perform(post("/login").param("username", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @SneakyThrows
    void testLogout() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("username")).thenReturn(USERNAME);

        final RequestPostProcessor requestPostProcessor = request -> {
            request.setSession(session);
            return request;
        };

        mockMvc.perform(get("/logout").with(requestPostProcessor))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }
}