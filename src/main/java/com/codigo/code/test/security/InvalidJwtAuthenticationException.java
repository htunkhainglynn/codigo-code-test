package com.codigo.code.test.security;

import java.io.Serial;

public class InvalidJwtAuthenticationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidJwtAuthenticationException(String message) {
        super(message);
    }
}
