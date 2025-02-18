package com.bione.api.ecommerce.dto;

import com.bione.api.ecommerce.enums.StatusPedido;  // 🔹 Corrigida a importação
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class StatusHistoryDTO {

    private Long id;

    @NotNull(message = "O ID do pedido é obrigatório")
    private Long pedidoId;

    @NotNull(message = "O status do pedido é obrigatório")
    @Builder.Default
    private StatusPedido status = StatusPedido.PENDENTE;  // 🔹 Agora compila corretamente!

    @NotNull(message = "A data de alteração é obrigatória")
    private LocalDateTime dataAlteracao;

    private String comentario;
}
