package com.localityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "suggestion_id", nullable = false, unique = true)
    private Suggestion suggestion;
    
    @Column(nullable = false, unique = true)
    private String firebaseChatId;
    
    @Column(nullable = false)
    private Integer messageCount = 0;
    
    @Column(nullable = false)
    private Integer participantCount = 0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime lastMessageAt;
}
