package org.galymzhan.financetrackerbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.ExceptionDto;
import org.galymzhan.financetrackerbackend.dto.UserProfileResponseDto;
import org.galymzhan.financetrackerbackend.dto.UserProfileUpdateDto;
import org.galymzhan.financetrackerbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User Profile", description = "Manage user profile information and settings")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user profile", description = "Retrieve current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getUserProfile() {
        UserProfileResponseDto dto = userService.getCurrentUserProfile();
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Update user profile", description = "Update current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized access",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponseDto> updateUserProfile(@Valid @RequestBody UserProfileUpdateDto updateDto) {
        UserProfileResponseDto dto = userService.updateCurrentUserProfile(updateDto);
        return ResponseEntity.ok(dto);
    }
}
