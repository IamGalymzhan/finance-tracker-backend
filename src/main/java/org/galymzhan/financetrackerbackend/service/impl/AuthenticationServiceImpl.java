package org.galymzhan.financetrackerbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.authentication.AuthenticationDto;
import org.galymzhan.financetrackerbackend.dto.authentication.LoginDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RegisterDto;
import org.galymzhan.financetrackerbackend.entity.Role;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.UsernameAlreadyExistsException;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.galymzhan.financetrackerbackend.service.CustomUserDetailsService;
import org.galymzhan.financetrackerbackend.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationDto register(RegisterDto registerDto) throws UsernameAlreadyExistsException {
        var user = User.builder()
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .email(registerDto.getEmail())
                .role(Role.ROLE_USER)
                .build();

        customUserDetailsService.create(user);

        var token = jwtUtil.generateToken(user);

        return AuthenticationDto.builder().token(token).role(Role.ROLE_USER.name()).build();
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

        var token = jwtUtil.generateToken(user);

        var actualUser = (User) user;

        return AuthenticationDto.builder()
                .token(token)
                .role(actualUser.getRole().name())
                .build();
    }

}
