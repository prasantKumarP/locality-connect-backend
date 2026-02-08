package com.localityconnect.controller;

import com.localityconnect.dto.LocalityRequest;
import com.localityconnect.dto.LocalityResponse;
import com.localityconnect.service.LocalityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/localities")
@RequiredArgsConstructor
public class LocalityController {
    
    private final LocalityService localityService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocalityResponse> createLocality(@Valid @RequestBody LocalityRequest request) {
        LocalityResponse response = localityService.createLocality(request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocalityResponse> updateLocality(
            @PathVariable Long id,
            @Valid @RequestBody LocalityRequest request) {
        LocalityResponse response = localityService.updateLocality(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LocalityResponse> getLocality(@PathVariable Long id) {
        LocalityResponse response = localityService.getLocality(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<LocalityResponse>> getAllLocalities() {
        List<LocalityResponse> localities = localityService.getAllLocalities();
        return ResponseEntity.ok(localities);
    }
}
