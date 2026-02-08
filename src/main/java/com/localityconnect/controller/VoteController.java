package com.localityconnect.controller;

import com.localityconnect.dto.ApiResponse;
import com.localityconnect.dto.VoteRequest;
import com.localityconnect.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {
    
    private final VoteService voteService;
    
    @PostMapping
    public ResponseEntity<ApiResponse> castVote(@Valid @RequestBody VoteRequest request) {
        ApiResponse response = voteService.castVote(request);
        return ResponseEntity.ok(response);
    }
}
