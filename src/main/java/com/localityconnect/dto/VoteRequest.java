package com.localityconnect.dto;

import com.localityconnect.entity.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {
    
    @NotNull(message = "Suggestion ID is required")
    private Long suggestionId;
    
    @NotNull(message = "Vote type is required")
    private VoteType voteType;
}
