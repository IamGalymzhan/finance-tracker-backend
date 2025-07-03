package org.galymzhan.financetrackerbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.config.JwtProperties;
import org.galymzhan.financetrackerbackend.dto.authentication.AuthenticationDto;
import org.galymzhan.financetrackerbackend.dto.authentication.LoginDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RefreshTokenDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RegisterDto;
import org.galymzhan.financetrackerbackend.entity.Role;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.AuthenticationException;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.galymzhan.financetrackerbackend.service.CustomUserDetailsService;
import org.galymzhan.financetrackerbackend.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;

    @Override
    public AuthenticationDto register(RegisterDto registerDto) {
        var user = User.builder()
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .email(registerDto.getEmail())
                .role(Role.ROLE_USER)
                .build();

        customUserDetailsService.create(user);

        return createAuthenticationResponse(user);
    }

    @Override
    public AuthenticationDto login(LoginDto loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        ));

        var user = customUserDetailsService
                .userDetailsService()
                .loadUserByUsername(loginDto.getUsername());

        return createAuthenticationResponse((User) user);
    }

    @Override
    public AuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getRefreshToken();

        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new AuthenticationException("Invalid refresh token");
        }

        String username = jwtUtil.extractUserName(refreshToken);

        var userDetails = customUserDetailsService
                .userDetailsService()
                .loadUserByUsername(username);

        if (!jwtUtil.isTokenValid(refreshToken, userDetails)) {
            throw new AuthenticationException("Refresh token is expired or invalid");
        }

        return createAuthenticationResponse((User) userDetails);
    }

    @Override
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new AuthenticationException("No authenticated user found");
    }

    @Override
    public String generateDevToken(String username) {
        var userDetails = customUserDetailsService
                .userDetailsService()
                .loadUserByUsername(username);
        
        return jwtUtil.generateDevToken(userDetails);
    }

    private AuthenticationDto createAuthenticationResponse(User user) {
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return AuthenticationDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .expiresIn(jwtProperties.getExpirationSeconds())
                .build();
    }
}
