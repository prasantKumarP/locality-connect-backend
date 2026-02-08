package com.localityconnect.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalityRequest {
    
    @NotBlank(message = "Locality name is required")
    private String name;
    
    private String description;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String pincode;
    
    @NotNull(message = "Voting threshold percentage is required")
    @Min(value = 1, message = "Voting threshold must be at least 1%")
    @Max(value = 100, message = "Voting threshold cannot exceed 100%")
    private Integer votingThresholdPercentage;
    
    @NotNull(message = "Voting period is required")
    @Min(value = 1, message = "Voting period must be at least 1 day")
    private Integer votingPeriodDays;
}
