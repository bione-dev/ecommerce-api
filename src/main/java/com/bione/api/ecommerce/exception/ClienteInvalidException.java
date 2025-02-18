package com.bione.api.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Retorna 400 Bad Request automaticamente
public class ClienteInvalidException extends RuntimeException {

    private static final long serialVersionUID = 1L;  // Evita problemas de serialização

    public ClienteInvalidException(String message) {
        super(message);
    }

    public ClienteInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
