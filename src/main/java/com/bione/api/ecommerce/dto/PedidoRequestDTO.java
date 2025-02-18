package com.bione.api.ecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDTO {

    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clienteId;

    @NotEmpty(message = "A lista de produtos e quantidades não pode estar vazia")
    private List<ProdutoQuantidadeDTO> produtos;  // Lista de produtos com quantidades
}
