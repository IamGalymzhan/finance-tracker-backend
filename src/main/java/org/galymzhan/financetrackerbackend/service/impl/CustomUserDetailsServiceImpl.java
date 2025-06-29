package org.galymzhan.financetrackerbackend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.UsernameAlreadyExistsException;
import org.galymzhan.financetrackerbackend.repository.UserRepository;
import org.galymzhan.financetrackerbackend.service.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    @Override
    @Transactional
    public User create(User user) throws UsernameAlreadyExistsException {
        var opt = userRepository.findByUsername(user.getUsername());
        if (opt.isPresent()) {
            throw new UsernameAlreadyExistsException(
                    "User with this username already exists"
            );
        }
        return userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username was not found")
        );
    }

}
