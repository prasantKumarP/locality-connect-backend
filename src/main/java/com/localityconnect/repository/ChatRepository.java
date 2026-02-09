package com.localityconnect.repository;

import com.localityconnect.entity.Chat;
import com.localityconnect.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    Optional<Chat> findBySuggestion(Suggestion suggestion);
    
    Optional<Chat> findByFirebaseChatId(String firebaseChatId);
}
