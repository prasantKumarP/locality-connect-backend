package com.localityconnect.service;

import com.google.firebase.database.*;
import com.localityconnect.dto.ChatMessageDTO;
import com.localityconnect.dto.ChatResponse;
import com.localityconnect.entity.Chat;
import com.localityconnect.entity.Suggestion;
import com.localityconnect.entity.User;
import com.localityconnect.exception.ResourceNotFoundException;
import com.localityconnect.repository.ChatRepository;
import com.localityconnect.repository.SuggestionRepository;
import com.localityconnect.repository.UserRepository;
import com.localityconnect.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatRepository chatRepository;
    private final SuggestionRepository suggestionRepository;
    private final UserRepository userRepository;
    
    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    @Transactional
    public ChatResponse getOrCreateChat(Long suggestionId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found"));
        
        // Check if chat already exists
        Chat chat = chatRepository.findBySuggestion(suggestion).orElse(null);
        
        if (chat == null) {
            // Create new chat
            chat = new Chat();
            chat.setSuggestion(suggestion);
            chat.setFirebaseChatId("chat_" + suggestionId + "_" + UUID.randomUUID().toString().substring(0, 8));
            chat.setMessageCount(0);
            chat.setParticipantCount(0);
            chat = chatRepository.save(chat);
            
            // Initialize Firebase chat room
            initializeFirebaseChat(chat);
        }
        
        return mapToResponse(chat);
    }
    
    private void initializeFirebaseChat(Chat chat) {
        try {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference chatRef = database.child("chats").child(chat.getFirebaseChatId());
            
            // Set initial chat metadata
            Map<String, Object> chatData = new HashMap<>();
            chatData.put("suggestionId", chat.getSuggestion().getId());
            chatData.put("suggestionTitle", chat.getSuggestion().getTitle());
            chatData.put("localityId", chat.getSuggestion().getLocality().getId());
            chatData.put("createdAt", System.currentTimeMillis());
            
            chatRef.setValueAsync(chatData);
        } catch (Exception e) {
            System.err.println("Error initializing Firebase chat: " + e.getMessage());
        }
    }
    
    @Transactional
    public void sendMessage(Long suggestionId, String message) {
        User currentUser = getCurrentUser();
        
        Chat chat = chatRepository.findBySuggestion(
                suggestionRepository.findById(suggestionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found"))
        ).orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
        
        try {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference messagesRef = database.child("chats")
                    .child(chat.getFirebaseChatId())
                    .child("messages");
            
            // Create message data
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("userId", currentUser.getId());
            messageData.put("username", currentUser.getUsername());
            messageData.put("text", message);
            messageData.put("timestamp", System.currentTimeMillis());
            
            // Push message to Firebase
            messagesRef.push().setValueAsync(messageData);
            
            // Update chat metadata
            chat.setMessageCount(chat.getMessageCount() + 1);
            chat.setLastMessageAt(LocalDateTime.now());
            chatRepository.save(chat);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
    }
    
    public ChatResponse getChatBySuggestionId(Long suggestionId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found"));
        
        Chat chat = chatRepository.findBySuggestion(suggestion)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found for this suggestion"));
        
        return mapToResponse(chat);
    }
    
    private ChatResponse mapToResponse(Chat chat) {
        ChatResponse response = new ChatResponse();
        response.setId(chat.getId());
        response.setSuggestionId(chat.getSuggestion().getId());
        response.setFirebaseChatId(chat.getFirebaseChatId());
        response.setMessageCount(chat.getMessageCount());
        response.setParticipantCount(chat.getParticipantCount());
        response.setCreatedAt(chat.getCreatedAt());
        response.setLastMessageAt(chat.getLastMessageAt());
        return response;
    }
}
