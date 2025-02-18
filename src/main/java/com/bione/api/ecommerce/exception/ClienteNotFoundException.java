package com.bione.api.ecommerce.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND) // Retorna automaticamente 404 Not Found no Spring Boot
public class ClienteNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L; // Evita problemas de serialização

    private final String errorCode;
    private final Long details; // Representa o ID do cliente

    public ClienteNotFoundException(String message) {
        super(message);
        this.errorCode = "CLIENTE_NOT_FOUND";
        this.details = null;
    }

    public ClienteNotFoundException(String message, Long id) {
        super(message);
        this.errorCode = "CLIENTE_NOT_FOUND";
        this.details = id;
    }

    public ClienteNotFoundException(String message, String errorCode, Long details) {
        super(message);
        this.errorCode = errorCode != null ? errorCode : "CLIENTE_NOT_FOUND";
        this.details = details;
    }

    public ClienteNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "CLIENTE_NOT_FOUND";
        this.details = null;
    }
}
