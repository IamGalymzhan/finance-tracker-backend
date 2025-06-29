package org.galymzhan.financetrackerbackend.exceptions;

import lombok.Getter;
import org.galymzhan.financetrackerbackend.util.ErrorCodeUtil;

@Getter
public class ErrorCodeException extends RuntimeException {
    
    private final ErrorCodeUtil errorCode;

    public ErrorCodeException(ErrorCodeUtil errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }
}