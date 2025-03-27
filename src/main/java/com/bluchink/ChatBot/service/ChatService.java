package com.bluchink.ChatBot.service;

import com.bluchink.ChatBot.dto.GeminiRequestDto;
import com.bluchink.ChatBot.dto.GeminiResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class ChatService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Autowired
    public ChatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    public String generateResponse(String prompt) {
        String url = GEMINI_API_URL + apiKey;

        GeminiRequestDto request = new GeminiRequestDto(
                Collections.singletonList(new GeminiRequestDto.Content(
                        Collections.singletonList(new GeminiRequestDto.Part(prompt))
                ))
        );

        GeminiResponseDto response = restTemplate.postForObject(url, request, GeminiResponseDto.class);

        if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
            return response.getCandidates().get(0).getContent().getParts().get(0).getText();
        }

        return "No response from Gemini API.";
    }
}