package com.localityconnect.service;

import com.localityconnect.dto.SuggestionRequest;
import com.localityconnect.dto.SuggestionResponse;
import com.localityconnect.entity.*;
import com.localityconnect.exception.ResourceNotFoundException;
import com.localityconnect.repository.LocalityRepository;
import com.localityconnect.repository.SuggestionRepository;
import com.localityconnect.repository.UserRepository;
import com.localityconnect.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    
    private final SuggestionRepository suggestionRepository;
    private final UserRepository userRepository;
    private final LocalityRepository localityRepository;
    
    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    @Transactional
    public SuggestionResponse createSuggestion(SuggestionRequest request) {
        User currentUser = getCurrentUser();
        
        // Check if user already has 5 NEW suggestions
        Long newSuggestionsCount = suggestionRepository.countByUserAndStatus(currentUser, SuggestionStatus.NEW);
        if (newSuggestionsCount >= 5) {
            throw new RuntimeException("You can only have maximum 5 NEW suggestions at a time");
        }
        
        Suggestion suggestion = new Suggestion();
        suggestion.setTitle(request.getTitle());
        suggestion.setDescription(request.getDescription());
        suggestion.setCategory(request.getCategory());
        suggestion.setUserPriority(request.getUserPriority());
        suggestion.setStatus(SuggestionStatus.NEW);
        suggestion.setUser(currentUser);
        suggestion.setLocality(currentUser.getLocality());
        suggestion.setLikeCount(0);
        suggestion.setDislikeCount(0);
        
        Suggestion saved = suggestionRepository.save(suggestion);
        return mapToResponse(saved);
    }
    
    @Transactional
    public SuggestionResponse updateSuggestion(Long id, SuggestionRequest request) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found"));
        
        User currentUser = getCurrentUser();
        if (!suggestion.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own suggestions");
        }
        
        suggestion.setTitle(request.getTitle());
        suggestion.setDescription(request.getDescription());
        suggestion.setCategory(request.getCategory());
        suggestion.setUserPriority(request.getUserPriority());
        
        Suggestion updated = suggestionRepository.save(suggestion);
        return mapToResponse(updated);
    }
    
    @Transactional
    public void deleteSuggestion(Long id) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found"));
        
        User currentUser = getCurrentUser();
        if (!suggestion.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own suggestions");
        }
        
        suggestionRepository.delete(suggestion);
    }
    
    public List<SuggestionResponse> getMySuggestions() {
        User currentUser = getCurrentUser();
        return suggestionRepository.findByUserOrderByUserPriorityAsc(currentUser).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<SuggestionResponse> getDashboard() {
        User currentUser = getCurrentUser();
        Long localityId = currentUser.getLocality().getId();
        
        return suggestionRepository.findByLocalityIdAndStatusOrderByCreatedAtDesc(localityId, SuggestionStatus.NEW).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<SuggestionResponse> getDiscussionForum() {
        User currentUser = getCurrentUser();
        Long localityId = currentUser.getLocality().getId();
        
        return suggestionRepository.findByLocalityIdAndStatusOrderByCalculatedPriorityAsc(
                localityId, SuggestionStatus.IN_DISCUSSION).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateSuggestionStatus(Suggestion suggestion) {
        int totalVotes = suggestion.getLikeCount() + suggestion.getDislikeCount();
        
        if (totalVotes == 0) {
            return; // No votes yet
        }
        
        // Calculate like percentage
        double likePercentage = (double) suggestion.getLikeCount() / totalVotes * 100;
        double dislikePercentage = (double) suggestion.getDislikeCount() / totalVotes * 100;
        
        // Check if voting period has passed
        Locality locality = suggestion.getLocality();
        LocalDateTime cutoffDate = suggestion.getCreatedAt().plusDays(locality.getVotingPeriodDays());
        boolean votingPeriodPassed = LocalDateTime.now().isAfter(cutoffDate);
        
        // Move to IN_DISCUSSION if threshold met within voting period
        if (likePercentage >= locality.getVotingThresholdPercentage() && !votingPeriodPassed) {
            suggestion.setStatus(SuggestionStatus.IN_DISCUSSION);
            suggestion.setDiscussionStartedAt(LocalDateTime.now());
            suggestion.setCalculatedPriority(calculatePriority(suggestion.getLikeCount()));
        } 
        // After voting period, categorize based on votes
        else if (votingPeriodPassed && suggestion.getStatus() == SuggestionStatus.NEW) {
            if (likePercentage > dislikePercentage) {
                suggestion.setStatus(SuggestionStatus.VALID);
            } else if (dislikePercentage > likePercentage) {
                suggestion.setStatus(SuggestionStatus.INVALID);
            } else {
                suggestion.setStatus(SuggestionStatus.LATER);
            }
        }
        
        suggestionRepository.save(suggestion);
    }
    
    private Integer calculatePriority(Integer likeCount) {
        // Priority 1-5 based on like count (1 is highest priority)
        if (likeCount >= 100) return 1;
        if (likeCount >= 50) return 2;
        if (likeCount >= 25) return 3;
        if (likeCount >= 10) return 4;
        return 5;
    }
    
    private SuggestionResponse mapToResponse(Suggestion suggestion) {
        SuggestionResponse response = new SuggestionResponse();
        response.setId(suggestion.getId());
        response.setTitle(suggestion.getTitle());
        response.setDescription(suggestion.getDescription());
        response.setCategory(suggestion.getCategory());
        response.setStatus(suggestion.getStatus());
        response.setUserPriority(suggestion.getUserPriority());
        response.setCalculatedPriority(suggestion.getCalculatedPriority());
        response.setUserId(suggestion.getUser().getId());
        response.setUsername(suggestion.getUser().getUsername());
        response.setLocalityId(suggestion.getLocality().getId());
        response.setLocalityName(suggestion.getLocality().getName());
        response.setLikeCount(suggestion.getLikeCount());
        response.setDislikeCount(suggestion.getDislikeCount());
        response.setCreatedAt(suggestion.getCreatedAt());
        response.setDiscussionStartedAt(suggestion.getDiscussionStartedAt());
        return response;
    }
}
