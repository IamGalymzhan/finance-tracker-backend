package org.galymzhan.expensetrackerbackend.service;

import org.galymzhan.expensetrackerbackend.entity.User;
import org.galymzhan.expensetrackerbackend.exceptions.UsernameAlreadyExistsException;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService {
    UserDetailsService userDetailsService();
    User create(User user) throws UsernameAlreadyExistsException;
}
