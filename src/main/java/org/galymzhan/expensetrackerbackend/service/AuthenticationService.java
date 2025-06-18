package org.galymzhan.expensetrackerbackend.service;

import org.galymzhan.expensetrackerbackend.dto.authentication.*;
import org.galymzhan.expensetrackerbackend.exceptions.UsernameAlreadyExistsException;

public interface AuthenticationService {

    AuthenticationDto login(LoginDto loginDto);

    AuthenticationDto register(RegisterDto registerDto) throws UsernameAlreadyExistsException;
}

