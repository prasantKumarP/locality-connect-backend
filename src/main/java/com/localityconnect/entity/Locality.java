package com.localityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "localities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Locality {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 200)
    private String address;
    
    @Column(length = 50)
    private String city;
    
    @Column(length = 50)
    private String state;
    
    @Column(length = 10)
    private String pincode;
    
    // Configuration: Voting threshold percentage (default 50%)
    @Column(nullable = false)
    private Integer votingThresholdPercentage = 50;
    
    // Configuration: Voting period in days (default 30 days)
    @Column(nullable = false)
    private Integer votingPeriodDays = 30;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "locality", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<User> users = new HashSet<>();
    
    @OneToMany(mappedBy = "locality", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Suggestion> suggestions = new HashSet<>();
}
