package com.localityconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    private Long id;
    private Long suggestionId;
    private String firebaseChatId;
    private Integer messageCount;
    private Integer participantCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
}
