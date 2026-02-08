package com.localityconnect.repository;

import com.localityconnect.entity.Suggestion;
import com.localityconnect.entity.SuggestionStatus;
import com.localityconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    
    // Count NEW status suggestions for a user
    Long countByUserAndStatus(User user, SuggestionStatus status);
    
    // Get all suggestions by locality and status
    List<Suggestion> findByLocalityIdAndStatusOrderByCreatedAtDesc(Long localityId, SuggestionStatus status);
    
    // Get user's suggestions ordered by their priority
    List<Suggestion> findByUserOrderByUserPriorityAsc(User user);
    
    // Get suggestions in discussion ordered by calculated priority
    List<Suggestion> findByLocalityIdAndStatusOrderByCalculatedPriorityAsc(Long localityId, SuggestionStatus status);
    
    // Get all NEW suggestions created before a certain time
    @Query("SELECT s FROM Suggestion s WHERE s.status = 'NEW' AND s.createdAt < :cutoffDate")
    List<Suggestion> findNewSuggestionsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}
