package org.galymzhan.financetrackerbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.UserProfileResponseDto;
import org.galymzhan.financetrackerbackend.dto.UserProfileUpdateDto;
import org.galymzhan.financetrackerbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile() {
        UserProfileResponseDto dto = userService.getCurrentUserProfile();
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UserProfileUpdateDto updateDto) {
        UserProfileResponseDto dto = userService.updateCurrentUserProfile(updateDto);
        return ResponseEntity.ok(dto);
    }
}
