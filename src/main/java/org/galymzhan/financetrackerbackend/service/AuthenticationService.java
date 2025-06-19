package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.authentication.*;
import org.galymzhan.financetrackerbackend.exceptions.UsernameAlreadyExistsException;

public interface AuthenticationService {

    AuthenticationDto login(LoginDto loginDto);

    AuthenticationDto register(RegisterDto registerDto) throws UsernameAlreadyExistsException;
}

