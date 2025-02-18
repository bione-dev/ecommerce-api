package com.bione.api.ecommerce.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;  // Nome do recurso que não foi encontrado
    private final Long resourceId;  // ID do recurso (opcional)

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = "Recurso";
        this.resourceId = null;
        log.warn("Recurso não encontrado: {}", message);
    }

    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super(resourceName + " com ID " + resourceId + " não foi encontrado.");
        this.resourceName = resourceName;
        this.resourceId = resourceId;
        log.warn("{} não encontrado - ID: {}", resourceName, resourceId);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.resourceName = "Recurso";
        this.resourceId = null;
        log.error("Erro ao buscar recurso: {} | Causa: {}", message, cause.getMessage());
    }

    public ResourceNotFoundException(String resourceName, Long resourceId, Throwable cause) {
        super(resourceName + " com ID " + resourceId + " não foi encontrado.", cause);
        this.resourceName = resourceName;
        this.resourceId = resourceId;
        log.error("{} não encontrado - ID: {} | Causa: {}", resourceName, resourceId, cause.getMessage());
    }
}
