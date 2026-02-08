package com.localityconnect.service;

import com.localityconnect.dto.JwtResponse;
import com.localityconnect.dto.LoginRequest;
import com.localityconnect.dto.RegisterRequest;
import com.localityconnect.entity.Locality;
import com.localityconnect.entity.User;
import com.localityconnect.entity.UserRole;
import com.localityconnect.exception.ResourceNotFoundException;
import com.localityconnect.repository.LocalityRepository;
import com.localityconnect.repository.UserRepository;
import com.localityconnect.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final LocalityRepository localityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        
        Locality locality = localityRepository.findById(request.getLocalityId())
                .orElseThrow(() -> new ResourceNotFoundException("Locality not found"));
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setLocality(locality);
        user.setRole(UserRole.RESIDENT);
        user.setActive(true);
        
        User savedUser = userRepository.save(user);
        
        // Auto-login after registration
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        return new JwtResponse(
            jwt,
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getRole().name(),
            savedUser.getLocality().getId()
        );
    }
    
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new JwtResponse(
            jwt,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().name(),
            user.getLocality().getId()
        );
    }
}
