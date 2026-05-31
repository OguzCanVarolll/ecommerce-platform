package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.domain.User;
import com.ecommerce.auth_service.domain.UserRole;
import com.ecommerce.auth_service.dto.AuthResponse;
import com.ecommerce.auth_service.dto.LoginRequest;
import com.ecommerce.auth_service.dto.RefreshTokenRequest;
import com.ecommerce.auth_service.dto.RegisterRequest;
import com.ecommerce.auth_service.exception.EmailAlreadyExistException;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.security.user.SecurityUser;
import com.ecommerce.auth_service.service.AuthService;
import com.ecommerce.auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request){

        log.info("Register Started");

        if(userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistException(request.email());
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user); /
        log.info("User saved. ID: {}", savedUser.getId());


        UserDetails userDetails = new SecurityUser(savedUser);

        return new AuthResponse(
                jwtService.generateToken(userDetails),
                jwtService.generateRefreshToken(userDetails)
        );
    }

    @Override
    public AuthResponse login (LoginRequest request){
        log.info("Trying to login: {}", request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        SecurityUser securityUser  = (SecurityUser) authentication.getPrincipal();

        log.info("Login succsesful");

        return new AuthResponse(
                jwtService.generateToken(securityUser),
                jwtService.generateRefreshToken(securityUser)
        );
    }

}

