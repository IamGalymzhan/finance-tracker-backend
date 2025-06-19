package org.galymzhan.financetrackerbackend.service.impl;

import org.galymzhan.financetrackerbackend.dto.authentication.AuthenticationDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RegisterDto;
import org.galymzhan.financetrackerbackend.entity.Role;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.UsernameAlreadyExistsException;
import org.galymzhan.financetrackerbackend.service.CustomUserDetailsService;
import org.galymzhan.financetrackerbackend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    public void register_ShouldReturnAuthenticationDto_WhenValidInput() throws UsernameAlreadyExistsException {
        RegisterDto registerDto = RegisterDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        User savedUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userDetailsService.create(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthenticationDto result = authenticationService.register(registerDto);

        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");
        verify(userDetailsService).create(any(User.class));
        verify(jwtUtil).generateToken(any(User.class));
    }

    @Test
    public void register_ShouldThrowException_WhenUsernameExists() throws UsernameAlreadyExistsException {
        RegisterDto registerDto = RegisterDto.builder()
                .username("existinguser")
                .email("test@example.com")
                .password("password123")
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userDetailsService.create(any(User.class)))
                .thenThrow(new UsernameAlreadyExistsException("User exists"));

        assertThatThrownBy(() -> authenticationService.register(registerDto))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessage("User exists");
    }
}