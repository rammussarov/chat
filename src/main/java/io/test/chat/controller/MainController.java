package io.test.chat.controller;

import io.test.chat.enums.MessageType;
import io.test.chat.model.Message;
import io.test.chat.service.UsernameStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for handling login and logout.
 */

@Controller
@RequiredArgsConstructor
public class MainController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final UsernameStorage storage;

    @GetMapping({"/"})
    public String showMainPage(HttpServletRequest request, Model model) {
        final String username = (String) request.getSession().getAttribute("username");

        if (username == null || username.trim().isEmpty()) {
            return "redirect:/login";
        }

        storage.addNewUser(username);
        messagingTemplate.convertAndSend("/topic/userList", storage.getUsernames());

        model.addAttribute("username", username);
        return "chat";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest servletRequest, @RequestParam String username, Model model) {
        username = username.trim();

        if (username.isEmpty() || storage.isExist(username)) {
            model.addAttribute("error", "Username already exists in a system!");
            return "login";
        }

        storage.addNewUser(username);
        servletRequest.getSession().setAttribute("username", username);

        messagingTemplate.convertAndSend("/topic/userList", storage.getUsernames());

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest servletRequest) {
        final String username = (String) servletRequest.getSession().getAttribute("username");
        storage.removeUser(username.trim());
        servletRequest.getSession().invalidate();

        return "redirect:/login";
    }
}
