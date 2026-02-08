package com.localityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "suggestions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SuggestionCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SuggestionStatus status = SuggestionStatus.NEW;
    
    // User's own priority for their suggestions (1-5, where 1 is highest)
    @Column(nullable = false)
    private Integer userPriority;
    
    // System-calculated priority when moved to IN_DISCUSSION (1-5, based on likes)
    private Integer calculatedPriority;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locality_id", nullable = false)
    private Locality locality;
    
    @Column(nullable = false)
    private Integer likeCount = 0;
    
    @Column(nullable = false)
    private Integer dislikeCount = 0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Timestamp when status changed to IN_DISCUSSION
    private LocalDateTime discussionStartedAt;
    
    @OneToMany(mappedBy = "suggestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();
}
