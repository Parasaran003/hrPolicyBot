package com.hr.policy.bot.demo.controllers;

import com.hr.policy.bot.demo.Service.HrChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final HrChatService hrChatService;

    public ChatController(HrChatService hrChatService) {
        this.hrChatService = hrChatService;
    }

    @GetMapping("/api/chat")
    public Map<String, String> chat(@RequestParam String query) {
        try {
            String response = hrChatService.askQuestion(query);
            return Map.of("response", response);
        } catch (Exception e) {
            // Catch the 503 ServerException and return a safe JSON response
            return Map.of("error", "The AI model is currently experiencing high demand. Please try again in a few moments.");
        }
    }
}