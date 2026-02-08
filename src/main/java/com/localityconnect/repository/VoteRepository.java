package com.localityconnect.repository;

import com.localityconnect.entity.Suggestion;
import com.localityconnect.entity.User;
import com.localityconnect.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    Optional<Vote> findByUserAndSuggestion(User user, Suggestion suggestion);
    
    Boolean existsByUserAndSuggestion(User user, Suggestion suggestion);
}
