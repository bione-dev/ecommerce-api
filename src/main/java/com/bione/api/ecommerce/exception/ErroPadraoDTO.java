package com.bione.api.ecommerce.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor // Construtor sem argumentos (necessário para serialização)
@AllArgsConstructor // Construtor com todos os argumentos
@Builder // Facilita a criação de objetos usando o padrão Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Evita incluir valores nulos na resposta JSON
public class ErroPadraoDTO {

    private int status; // Código HTTP do erro
    private String mensagem; // Mensagem de erro
    private String detalhes; // Detalhes adicionais do erro
}
