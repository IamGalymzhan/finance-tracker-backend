package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.authentication.AuthenticationDto;
import org.galymzhan.financetrackerbackend.dto.authentication.LoginDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RefreshTokenDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RegisterDto;
import org.galymzhan.financetrackerbackend.entity.User;

public interface AuthenticationService {

    AuthenticationDto login(LoginDto loginDto);

    AuthenticationDto register(RegisterDto registerDto);
    
    AuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto);

    User getCurrentUser();
    
    /**
     * Generate a development token that effectively never expires
     * WARNING: Only use this for development/testing purposes!
     */
    String generateDevToken(String username);
}

