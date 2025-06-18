package org.galymzhan.expensetrackerbackend.util;


import org.galymzhan.expensetrackerbackend.dto.ExceptionDto;
import org.galymzhan.expensetrackerbackend.exceptions.ErrorCodeException;

public enum ErrorCodeUtil {
    ERR_USERNAME_ALREADY_EXISTS;

    public static ExceptionDto toExceptionDto(Exception exception) {
        if (exception instanceof ErrorCodeException e) {
            return ExceptionDto.builder()
                    .errorCode(e.getErrorCode())
                    .message(e.getMessage())
                    .build();
        }
        return ExceptionDto.builder()
                .message(exception.getMessage())
                .build();
    }
}
