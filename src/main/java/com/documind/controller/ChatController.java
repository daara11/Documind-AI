package com.documind.controller;

import com.documind.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public Map<String, String> ask(@RequestBody Map<String, String> request)
            throws Exception {

        String question = request.getOrDefault("question", "").trim();
        String mode = request.getOrDefault("mode", "pdf");
        String documentText = request.getOrDefault("documentText", "");

        if (question.isEmpty()) {
            throw new IllegalArgumentException("Please enter a question.");
        }

        String answer = chatService.answer(question, mode, documentText);

        return Map.of("answer", answer);
    }
}
