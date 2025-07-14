package org.galymzhan.financetrackerbackend.util;

import org.galymzhan.financetrackerbackend.BaseSpringTest;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest extends BaseSpringTest {

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .role(Role.ROLE_USER)
                .build();
        testUser.setId(1L);
    }

    @Test
    void generateToken_ShouldReturnValidToken_WhenValidUser() {
        // When
        String token = jwtUtil.generateToken(testUser);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(jwtUtil.extractUserName(token)).isEqualTo("testuser");
    }

    @Test
    void generateRefreshToken_ShouldReturnValidRefreshToken_WhenValidUser() {
        // When
        String refreshToken = jwtUtil.generateRefreshToken(testUser);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(jwtUtil.extractUserName(refreshToken)).isEqualTo("testuser");
        assertThat(jwtUtil.isRefreshToken(refreshToken)).isTrue();
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        boolean isValid = jwtUtil.isTokenValid(token, testUser);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsForDifferentUser() {
        // Given
        String token = jwtUtil.generateToken(testUser);
        User differentUser = User.builder()
                .username("differentuser")
                .build();

        // When
        boolean isValid = jwtUtil.isTokenValid(token, differentUser);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void extractUserId_ShouldReturnUserId_WhenValidToken() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        Long userId = jwtUtil.extractUserId(token);

        // Then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void extractRole_ShouldReturnRole_WhenValidToken() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        String role = jwtUtil.extractRole(token);

        // Then
        assertThat(role).isEqualTo("ROLE_USER");
    }

    @Test
    void isRefreshToken_ShouldReturnTrue_WhenRefreshToken() {
        // Given
        String refreshToken = jwtUtil.generateRefreshToken(testUser);

        // When
        boolean isRefresh = jwtUtil.isRefreshToken(refreshToken);

        // Then
        assertThat(isRefresh).isTrue();
    }

    @Test
    void isRefreshToken_ShouldReturnFalse_WhenAccessToken() {
        // Given
        String accessToken = jwtUtil.generateToken(testUser);

        // When
        boolean isRefresh = jwtUtil.isRefreshToken(accessToken);

        // Then
        assertThat(isRefresh).isFalse();
    }

    @Test
    void refreshTokenShouldBeValid_WhenGeneratedProperly() {
        // Given
        String refreshToken = jwtUtil.generateRefreshToken(testUser);

        // When
        boolean isValid = jwtUtil.isTokenValid(refreshToken, testUser);

        // Then
        assertThat(isValid).isTrue();
        assertThat(jwtUtil.isRefreshToken(refreshToken)).isTrue();
        assertThat(jwtUtil.extractUserName(refreshToken)).isEqualTo("testuser");
        assertThat(jwtUtil.extractUserId(refreshToken)).isEqualTo(1L);
    }
}