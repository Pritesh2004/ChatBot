package com.bluchink.ChatBot.controller;

import com.bluchink.ChatBot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/generate")
    public String getAIResponse(@RequestBody Map<String, String> request) {
        String userMessage = request.get("userMessage");
        return chatService.generateResponse(userMessage);
    }
}