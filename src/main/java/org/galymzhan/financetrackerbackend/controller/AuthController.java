package org.galymzhan.financetrackerbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.ExceptionDto;
import org.galymzhan.financetrackerbackend.dto.authentication.AuthenticationDto;
import org.galymzhan.financetrackerbackend.dto.authentication.LoginDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RefreshTokenDto;
import org.galymzhan.financetrackerbackend.dto.authentication.RegisterDto;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User registration, login, and token management")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register new user", description = "Create new user account with username, email, and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = AuthenticationDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
        @ApiResponse(responseCode = "409", description = "Username or email already exists",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationDto> register(@Valid @RequestBody RegisterDto registerDto) {
        AuthenticationDto dto = authenticationService.register(registerDto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "User login", description = "Authenticate user and return access/refresh tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthenticationDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationDto> login(@Valid @RequestBody LoginDto loginDto) {
        AuthenticationDto dto = authenticationService.login(loginDto);
        return ResponseEntity.ok(dto);
    }
    
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = AuthenticationDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationDto> refresh(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        AuthenticationDto dto = authenticationService.refreshToken(refreshTokenDto);
        return ResponseEntity.ok(dto);
    }
}
