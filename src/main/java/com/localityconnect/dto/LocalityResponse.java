package com.localityconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalityResponse {
    
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private Integer votingThresholdPercentage;
    private Integer votingPeriodDays;
    private Boolean active;
    private LocalDateTime createdAt;
}
