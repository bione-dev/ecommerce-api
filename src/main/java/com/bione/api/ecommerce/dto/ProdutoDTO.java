package com.bione.api.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
public class ProdutoDTO {
    private Long id;
    private String nome;
    private BigDecimal preco;
    private Integer estoque;


    // ✅ Construtor correto (aceita 4 parâmetros)
    public ProdutoDTO(Long id, String nome, BigDecimal preco, Integer estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }
}
