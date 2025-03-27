package com.bluchink.ChatBot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRequest {
    private Map<String, Object> inputs;
    private String query;
    private String response_mode;
    private String conversation_id;
    private String user;
    private List<ChatFile> files;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ChatFile {
    private String type;
    private String transfer_method;
    private String url;
}
