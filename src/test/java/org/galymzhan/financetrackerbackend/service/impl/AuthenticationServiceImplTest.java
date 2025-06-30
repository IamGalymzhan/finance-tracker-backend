package org.galymzhan.financetrackerbackend.service.impl;

import org.galymzhan.financetrackerbackend.config.JwtProperties;
import org.galymzhan.financetrackerbackend.dto.authentication.AuthenticationDto;
import org.galymzhan.financetrackerbackend.dto.authentication.LoginDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RefreshTokenDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RegisterDto;
import org.galymzhan.financetrackerbackend.entity.Role;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.AuthenticationException;
import org.galymzhan.financetrackerbackend.exceptions.UsernameAlreadyExistsException;
import org.galymzhan.financetrackerbackend.service.CustomUserDetailsService;
import org.galymzhan.financetrackerbackend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    public void register_ShouldReturnAuthenticationDto_WhenValidInput() throws UsernameAlreadyExistsException {
        // Given
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
        when(customUserDetailsService.create(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("refresh-token");
        when(jwtProperties.getExpirationSeconds()).thenReturn(3600L);

        // When
        AuthenticationDto result = authenticationService.register(registerDto);

        // Then
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");
        assertThat(result.getExpiresIn()).isEqualTo(3600L);
        verify(customUserDetailsService).create(any(User.class));
        verify(jwtUtil).generateToken(any(User.class));
        verify(jwtUtil).generateRefreshToken(any(User.class));
    }

    @Test
    public void register_ShouldThrowException_WhenUsernameExists() throws UsernameAlreadyExistsException {
        // Given
        RegisterDto registerDto = RegisterDto.builder()
                .username("existinguser")
                .email("test@example.com")
                .password("password123")
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(customUserDetailsService.create(any(User.class)))
                .thenThrow(new UsernameAlreadyExistsException("User exists"));

        // When & Then
        assertThatThrownBy(() -> authenticationService.register(registerDto))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessage("User exists");
    }
    
    @Test
    public void login_ShouldReturnAuthenticationDto_WhenValidCredentials() {
        // Given
        LoginDto loginDto = LoginDto.builder()
                .username("testuser")
                .password("password123")
                .build();

        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        // Mock the userDetailsService chain properly
        when(customUserDetailsService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(user);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("refresh-token");
        when(jwtProperties.getExpirationSeconds()).thenReturn(3600L);

        // When
        AuthenticationDto result = authenticationService.login(loginDto);

        // Then
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");
        assertThat(result.getExpiresIn()).isEqualTo(3600L);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
    
    @Test
    public void refreshToken_ShouldReturnNewTokens_WhenValidRefreshToken() {
        // Given
        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken("valid-refresh-token")
                .build();

        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        // Mock the userDetailsService chain properly
        when(customUserDetailsService.userDetailsService()).thenReturn(userDetailsService);
        when(jwtUtil.isRefreshToken("valid-refresh-token")).thenReturn(true);
        when(jwtUtil.extractUserName("valid-refresh-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(user);
        when(jwtUtil.isTokenValid("valid-refresh-token", user)).thenReturn(true);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("new-access-token");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("new-refresh-token");
        when(jwtProperties.getExpirationSeconds()).thenReturn(3600L);

        // When
        AuthenticationDto result = authenticationService.refreshToken(refreshTokenDto);

        // Then
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");
        assertThat(result.getExpiresIn()).isEqualTo(3600L);
    }
    
    @Test
    public void refreshToken_ShouldThrowException_WhenNotRefreshToken() {
        // Given
        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken("access-token")
                .build();

        when(jwtUtil.isRefreshToken("access-token")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshTokenDto))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid refresh token");
    }
    
    @Test
    public void refreshToken_ShouldThrowException_WhenRefreshTokenExpired() {
        // Given
        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken("expired-refresh-token")
                .build();

        User user = User.builder()
                .username("testuser")
                .build();

        // Mock the userDetailsService chain properly
        when(customUserDetailsService.userDetailsService()).thenReturn(userDetailsService);
        when(jwtUtil.isRefreshToken("expired-refresh-token")).thenReturn(true);
        when(jwtUtil.extractUserName("expired-refresh-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(user);
        when(jwtUtil.isTokenValid("expired-refresh-token", user)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshTokenDto))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Refresh token is expired or invalid");
    }
}