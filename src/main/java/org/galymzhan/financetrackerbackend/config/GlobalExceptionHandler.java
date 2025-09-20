package org.galymzhan.financetrackerbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.galymzhan.financetrackerbackend.dto.response.ExceptionDto;
import org.galymzhan.financetrackerbackend.exceptions.AuthenticationException;
import org.galymzhan.financetrackerbackend.exceptions.ErrorCodeException;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;
import org.galymzhan.financetrackerbackend.exceptions.UsernameAlreadyExistsException;
import org.galymzhan.financetrackerbackend.util.ErrorCodeUtil;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {} | URI: {}", ex.getMessage(), request.getDescription(false));
        ExceptionDto error = ExceptionDto.builder()
                .errorCode(ErrorCodeUtil.RESOURCE_NOT_FOUND.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ExceptionDto> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex, WebRequest request) {
        log.warn("Username already exists: {} | URI: {}", ex.getMessage(), request.getDescription(false));
        ExceptionDto error = ExceptionDto.builder()
                .errorCode(ErrorCodeUtil.USERNAME_ALREADY_EXISTS.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionDto> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        log.warn("Authentication failed: {} | URI: {}", ex.getMessage(), request.getDescription(false));
        ExceptionDto error = ExceptionDto.builder()
                .errorCode(ErrorCodeUtil.AUTHENTICATION_FAILED.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ExceptionDto> handleSecurityExceptions(Exception ex, WebRequest request) {
        log.warn("Security exception: {} | URI: {}", ex.getMessage(), request.getDescription(false));
        ExceptionDto error = ExceptionDto.builder()
                .errorCode(ErrorCodeUtil.INVALID_CREDENTIALS.getCode())
                .message("Invalid username or password")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDto> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {} | URI {}", ex.getMessage(), request.getDescription(false));
        ExceptionDto error = ExceptionDto.builder()
                .errorCode(ErrorCodeUtil.ACCESS_DENIED.getCode())
                .message("You don't have permission to access this resource")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation failed: {} | URI: {}", ex.getMessage(), request.getDescription(false));

        Map<String, Object> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ExceptionDto error = ExceptionDto.builder()
                .errorCode(ErrorCodeUtil.VALIDATION_FAILED.getCode())
                .message("Validation failed")
                .details(Map.of("fieldErrors", fieldErrors))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionDto> handleHandlerMethodValidationException(HandlerMethodValidationException ex, WebRequest request) {
        log.warn("Handler method validation failed: {} | URI: {}", ex.getMessage(), request.getDescription(false));

        ExceptionDto error = ExceptionDto.builder()
                .errorCode(ErrorCodeUtil.VALIDATION_FAILED.getCode())
                .message("Validation failed")
                .details(Map.of("validationErrors", ex.getAllErrors().stream()
                        .map(MessageSourceResolvable::getDefaultMessage)
                        .toList()))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ErrorCodeException.class)
    public ResponseEntity<ExceptionDto> handleErrorCodeException(ErrorCodeException ex, WebRequest request) {
        log.warn("Error code exception: {} - {} | URI: {}", ex.getErrorCode(), ex.getMessage(), request.getDescription(false));
        ExceptionDto error = ExceptionDto.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred {} | URI: {}", ex, request.getDescription(false));
        ExceptionDto error = ExceptionDto.builder()
                .errorCode(ErrorCodeUtil.INTERNAL_SERVER_ERROR.getCode())
                .message("An unexpected error occurred. Please try again later.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}