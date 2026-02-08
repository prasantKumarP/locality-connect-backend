package com.localityconnect.dto;

import com.localityconnect.entity.SuggestionCategory;
import com.localityconnect.entity.SuggestionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionResponse {
    
    private Long id;
    private String title;
    private String description;
    private SuggestionCategory category;
    private SuggestionStatus status;
    private Integer userPriority;
    private Integer calculatedPriority;
    private Long userId;
    private String username;
    private Long localityId;
    private String localityName;
    private Integer likeCount;
    private Integer dislikeCount;
    private LocalDateTime createdAt;
    private LocalDateTime discussionStartedAt;
}
