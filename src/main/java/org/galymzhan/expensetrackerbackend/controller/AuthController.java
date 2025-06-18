package org.galymzhan.expensetrackerbackend.controller;

import lombok.RequiredArgsConstructor;
import org.galymzhan.expensetrackerbackend.dto.authentication.LoginDto;
import org.galymzhan.expensetrackerbackend.dto.authentication.RegisterDto;
import org.galymzhan.expensetrackerbackend.service.AuthenticationService;
import org.galymzhan.expensetrackerbackend.util.ErrorCodeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        try {
            var response = authenticationService.register(registerDto);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ErrorCodeUtil.toExceptionDto(e));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            var response = authenticationService.login(loginDto);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ErrorCodeUtil.toExceptionDto(e));
        }
    }
}
