package org.galymzhan.financetrackerbackend.util;


import org.galymzhan.financetrackerbackend.dto.ExceptionDto;
import org.galymzhan.financetrackerbackend.exceptions.ErrorCodeException;

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
