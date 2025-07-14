package org.galymzhan.financetrackerbackend.service.impl;

import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.entity.enums.Role;
import org.galymzhan.financetrackerbackend.exceptions.UsernameAlreadyExistsException;
import org.galymzhan.financetrackerbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsServiceImpl customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void create_ShouldReturnSavedUser_WhenUsernameDoesNotExist() throws UsernameAlreadyExistsException {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User userToCreate = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .role(Role.ROLE_USER)
                .build();

        User result = customUserDetailsService.create(userToCreate);

        assertThat(result).isNotNull();
        verify(userRepository).save(userToCreate);
    }

    @Test
    void create_ShouldThrowUsernameAlreadyExistsException_WhenUsernameExists() {
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(testUser));

        User userToCreate = User.builder()
                .username("existinguser")
                .build();

        assertThatThrownBy(() -> customUserDetailsService.create(userToCreate))
                .isInstanceOf(UsernameAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getByUsername_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = customUserDetailsService.getByUsername("testuser");

        assertThat(result).isEqualTo(testUser);
    }

    @Test
    void getByUsername_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.getByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
