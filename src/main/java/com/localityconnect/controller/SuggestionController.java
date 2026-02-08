package com.localityconnect.controller;

import com.localityconnect.dto.ApiResponse;
import com.localityconnect.dto.SuggestionRequest;
import com.localityconnect.dto.SuggestionResponse;
import com.localityconnect.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class SuggestionController {
    
    private final SuggestionService suggestionService;
    
    @PostMapping
    public ResponseEntity<SuggestionResponse> createSuggestion(@Valid @RequestBody SuggestionRequest request) {
        SuggestionResponse response = suggestionService.createSuggestion(request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SuggestionResponse> updateSuggestion(
            @PathVariable Long id,
            @Valid @RequestBody SuggestionRequest request) {
        SuggestionResponse response = suggestionService.updateSuggestion(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSuggestion(@PathVariable Long id) {
        suggestionService.deleteSuggestion(id);
        return ResponseEntity.ok(ApiResponse.success("Suggestion deleted successfully"));
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<SuggestionResponse>> getMySuggestions() {
        List<SuggestionResponse> suggestions = suggestionService.getMySuggestions();
        return ResponseEntity.ok(suggestions);
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<List<SuggestionResponse>> getDashboard() {
        List<SuggestionResponse> suggestions = suggestionService.getDashboard();
        return ResponseEntity.ok(suggestions);
    }
    
    @GetMapping("/discussion")
    public ResponseEntity<List<SuggestionResponse>> getDiscussionForum() {
        List<SuggestionResponse> suggestions = suggestionService.getDiscussionForum();
        return ResponseEntity.ok(suggestions);
    }
}
