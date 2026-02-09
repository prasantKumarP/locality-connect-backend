package com.localityconnect.controller;

import com.localityconnect.dto.ApiResponse;
import com.localityconnect.dto.ChatResponse;
import com.localityconnect.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    @GetMapping("/suggestion/{suggestionId}")
    public ResponseEntity<ChatResponse> getOrCreateChat(@PathVariable Long suggestionId) {
        ChatResponse response = chatService.getOrCreateChat(suggestionId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/suggestion/{suggestionId}/message")
    public ResponseEntity<ApiResponse> sendMessage(
            @PathVariable Long suggestionId,
            @RequestBody Map<String, String> payload) {
        
        String message = payload.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Message cannot be empty"));
        }
        
        chatService.sendMessage(suggestionId, message);
        return ResponseEntity.ok(ApiResponse.success("Message sent successfully"));
    }
}
