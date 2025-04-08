package com.bluchink.ChatBot.controller;

import com.bluchink.ChatBot.dto.ChatRequestDto;
import com.bluchink.ChatBot.dto.ChatResponse;
import com.bluchink.ChatBot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatRequestDto requestDto) {
        Flux<ChatResponse> chatResponse = chatService.streamChatbotResponse(requestDto.getQuery());
        return chatResponse.map(ChatResponse::getAnswer);
    }

}
