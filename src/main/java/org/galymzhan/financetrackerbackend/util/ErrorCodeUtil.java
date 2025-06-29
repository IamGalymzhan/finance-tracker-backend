package org.galymzhan.financetrackerbackend.util;


import lombok.Getter;

@Getter
public enum ErrorCodeUtil {

    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
    AUTHENTICATION_FAILED("AUTHENTICATION_FAILED"),
    VALIDATION_FAILED("VALIDATION_FAILED"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
    ACCESS_DENIED("ACCESS_DENIED"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR");

    private final String code;

    ErrorCodeUtil(String code) {
        this.code = code;
    }
}
