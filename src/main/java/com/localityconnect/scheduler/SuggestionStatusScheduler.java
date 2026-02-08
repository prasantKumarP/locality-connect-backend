package com.localityconnect.scheduler;

import com.localityconnect.entity.Suggestion;
import com.localityconnect.entity.SuggestionStatus;
import com.localityconnect.repository.SuggestionRepository;
import com.localityconnect.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuggestionStatusScheduler {
    
    private final SuggestionRepository suggestionRepository;
    private final SuggestionService suggestionService;
    
    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    public void updateExpiredSuggestions() {
        log.info("Running scheduled task to update suggestion statuses");
        
        LocalDateTime now = LocalDateTime.now();
        List<Suggestion> allNewSuggestions = suggestionRepository.findByLocalityIdAndStatusOrderByCreatedAtDesc(null, SuggestionStatus.NEW);
        
        for (Suggestion suggestion : allNewSuggestions) {
            LocalDateTime cutoffDate = suggestion.getCreatedAt()
                    .plusDays(suggestion.getLocality().getVotingPeriodDays());
            
            if (now.isAfter(cutoffDate)) {
                log.info("Processing expired suggestion: {}", suggestion.getId());
                suggestionService.updateSuggestionStatus(suggestion);
            }
        }
        
        log.info("Scheduled task completed");
    }
}
