package com.localityconnect.service;

import com.localityconnect.dto.ApiResponse;
import com.localityconnect.dto.VoteRequest;
import com.localityconnect.entity.Suggestion;
import com.localityconnect.entity.User;
import com.localityconnect.entity.Vote;
import com.localityconnect.entity.VoteType;
import com.localityconnect.exception.ResourceNotFoundException;
import com.localityconnect.repository.SuggestionRepository;
import com.localityconnect.repository.UserRepository;
import com.localityconnect.repository.VoteRepository;
import com.localityconnect.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {
    
    private final VoteRepository voteRepository;
    private final SuggestionRepository suggestionRepository;
    private final UserRepository userRepository;
    private final SuggestionService suggestionService;
    
    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    @Transactional
    public ApiResponse castVote(VoteRequest request) {
        User currentUser = getCurrentUser();
        Suggestion suggestion = suggestionRepository.findById(request.getSuggestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found"));
        
        // Check if user is from the same locality
        if (!suggestion.getLocality().getId().equals(currentUser.getLocality().getId())) {
            throw new RuntimeException("You can only vote on suggestions from your locality");
        }
        
        // Check if user already voted
        Vote existingVote = voteRepository.findByUserAndSuggestion(currentUser, suggestion).orElse(null);
        
        if (existingVote != null) {
            // If same vote type, remove the vote (toggle)
            if (existingVote.getVoteType() == request.getVoteType()) {
                // Remove vote
                if (existingVote.getVoteType() == VoteType.LIKE) {
                    suggestion.setLikeCount(suggestion.getLikeCount() - 1);
                } else {
                    suggestion.setDislikeCount(suggestion.getDislikeCount() - 1);
                }
                voteRepository.delete(existingVote);
                suggestionRepository.save(suggestion);
                suggestionService.updateSuggestionStatus(suggestion);
                return ApiResponse.success("Vote removed successfully");
            } else {
                // Change vote type
                if (existingVote.getVoteType() == VoteType.LIKE) {
                    suggestion.setLikeCount(suggestion.getLikeCount() - 1);
                    suggestion.setDislikeCount(suggestion.getDislikeCount() + 1);
                } else {
                    suggestion.setDislikeCount(suggestion.getDislikeCount() - 1);
                    suggestion.setLikeCount(suggestion.getLikeCount() + 1);
                }
                existingVote.setVoteType(request.getVoteType());
                voteRepository.save(existingVote);
                suggestionRepository.save(suggestion);
                suggestionService.updateSuggestionStatus(suggestion);
                return ApiResponse.success("Vote changed successfully");
            }
        }
        
        // New vote
        Vote vote = new Vote();
        vote.setUser(currentUser);
        vote.setSuggestion(suggestion);
        vote.setVoteType(request.getVoteType());
        voteRepository.save(vote);
        
        // Update suggestion counts
        if (request.getVoteType() == VoteType.LIKE) {
            suggestion.setLikeCount(suggestion.getLikeCount() + 1);
        } else {
            suggestion.setDislikeCount(suggestion.getDislikeCount() + 1);
        }
        suggestionRepository.save(suggestion);
        
        // Check if status should be updated
        suggestionService.updateSuggestionStatus(suggestion);
        
        return ApiResponse.success("Vote cast successfully");
    }
}
