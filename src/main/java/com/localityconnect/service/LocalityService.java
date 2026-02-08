package com.localityconnect.service;

import com.localityconnect.dto.LocalityRequest;
import com.localityconnect.dto.LocalityResponse;
import com.localityconnect.entity.Locality;
import com.localityconnect.exception.ResourceNotFoundException;
import com.localityconnect.repository.LocalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalityService {
    
    private final LocalityRepository localityRepository;
    
    @Transactional
    public LocalityResponse createLocality(LocalityRequest request) {
        if (localityRepository.existsByName(request.getName())) {
            throw new RuntimeException("Locality with this name already exists");
        }
        
        Locality locality = new Locality();
        locality.setName(request.getName());
        locality.setDescription(request.getDescription());
        locality.setAddress(request.getAddress());
        locality.setCity(request.getCity());
        locality.setState(request.getState());
        locality.setPincode(request.getPincode());
        locality.setVotingThresholdPercentage(request.getVotingThresholdPercentage());
        locality.setVotingPeriodDays(request.getVotingPeriodDays());
        locality.setActive(true);
        
        Locality saved = localityRepository.save(locality);
        return mapToResponse(saved);
    }
    
    @Transactional
    public LocalityResponse updateLocality(Long id, LocalityRequest request) {
        Locality locality = localityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Locality not found"));
        
        locality.setDescription(request.getDescription());
        locality.setAddress(request.getAddress());
        locality.setCity(request.getCity());
        locality.setState(request.getState());
        locality.setPincode(request.getPincode());
        locality.setVotingThresholdPercentage(request.getVotingThresholdPercentage());
        locality.setVotingPeriodDays(request.getVotingPeriodDays());
        
        Locality updated = localityRepository.save(locality);
        return mapToResponse(updated);
    }
    
    public LocalityResponse getLocality(Long id) {
        Locality locality = localityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Locality not found"));
        return mapToResponse(locality);
    }
    
    public List<LocalityResponse> getAllLocalities() {
        return localityRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private LocalityResponse mapToResponse(Locality locality) {
        LocalityResponse response = new LocalityResponse();
        response.setId(locality.getId());
        response.setName(locality.getName());
        response.setDescription(locality.getDescription());
        response.setAddress(locality.getAddress());
        response.setCity(locality.getCity());
        response.setState(locality.getState());
        response.setPincode(locality.getPincode());
        response.setVotingThresholdPercentage(locality.getVotingThresholdPercentage());
        response.setVotingPeriodDays(locality.getVotingPeriodDays());
        response.setActive(locality.getActive());
        response.setCreatedAt(locality.getCreatedAt());
        return response;
    }
}
