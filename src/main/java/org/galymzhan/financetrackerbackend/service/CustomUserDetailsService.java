package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService {
    UserDetailsService userDetailsService();
    User create(User user);
}
