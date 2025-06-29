package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.authentication.AuthenticationDto;
import org.galymzhan.financetrackerbackend.dto.authentication.LoginDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RegisterDto;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.UsernameAlreadyExistsException;

public interface AuthenticationService {

    AuthenticationDto login(LoginDto loginDto);

    AuthenticationDto register(RegisterDto registerDto) throws UsernameAlreadyExistsException;

    User getCurrentUser();
}

