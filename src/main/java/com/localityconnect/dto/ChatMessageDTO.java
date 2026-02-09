package com.localityconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    
    private String id;
    private String text;
    private Long userId;
    private String username;
    private Long timestamp;
}
