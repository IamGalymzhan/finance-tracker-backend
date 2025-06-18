package org.galymzhan.expensetrackerbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.galymzhan.expensetrackerbackend.dto.authentication.AuthenticationDto;
import org.galymzhan.expensetrackerbackend.dto.authentication.LoginDto;
import org.galymzhan.expensetrackerbackend.dto.authentication.RegisterDto;
import org.galymzhan.expensetrackerbackend.entity.Role;
import org.galymzhan.expensetrackerbackend.entity.User;
import org.galymzhan.expensetrackerbackend.exceptions.UsernameAlreadyExistsException;
import org.galymzhan.expensetrackerbackend.service.AuthenticationService;
import org.galymzhan.expensetrackerbackend.service.CustomUserDetailsService;
import org.galymzhan.expensetrackerbackend.util.JwtUtil;
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
