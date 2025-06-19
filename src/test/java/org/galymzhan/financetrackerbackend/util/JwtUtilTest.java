package org.galymzhan.financetrackerbackend.util;

import org.galymzhan.financetrackerbackend.BaseSpringTest;
import org.galymzhan.financetrackerbackend.entity.Role;
import org.galymzhan.financetrackerbackend.entity.User;
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
}