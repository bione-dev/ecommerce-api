package com.bione.api.ecommerce.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusPedido {
    EM_ANDAMENTO,
    FINALIZADO,
    CANCELADO,
    PENDENTE;

    @JsonValue
    public String toValue() {
        return this.name();  // Retorna o nome do enum em formato string
    }
}
