package com.bione.api.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ProdutoQuantidadeDTO {

    @NotNull(message = "O ID do produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "A quantidade do produto é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private Integer quantidade;
}
