package com.bione.api.ecommerce.dto;

import com.bione.api.ecommerce.model.Pedido;
import com.bione.api.ecommerce.model.PedidoProduto;
import com.bione.api.ecommerce.enums.StatusPedido;  // Importando o enum
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDTO {
    private Long id;
    private Long clienteId;
    private List<ProdutoDTO> produtos;
    private BigDecimal total;
    private LocalDateTime dataPedido;

    // Mudança para StatusPedido (enum) ao invés de String
    private StatusPedido status;  // Agora o status é do tipo StatusPedido (enum)

    private String enderecoEntrega;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    // ✅ Método para converter Pedido -> PedidoDTO
    public static PedidoDTO fromEntity(Pedido pedido) {
        // Garantindo a conversão do status para StatusPedido (enum)
        StatusPedido statusEnum = StatusPedido.valueOf(pedido.getStatus());  // Converte de String para StatusPedido (enum)

        return PedidoDTO.builder()
                .id(pedido.getId())
                .clienteId(pedido.getCliente().getId())
                .produtos(pedido.getItensPedido().stream()  // ✅ Usa 'getItensPedido()' corretamente
                        .map(PedidoProduto::toProdutoDTO)  // ✅ Agora esse método existe e funciona!
                        .collect(Collectors.toList()))
                .total(pedido.getTotal())
                .dataPedido(pedido.getDataPedido())
                .status(statusEnum)  // Usa o status convertido como enum
                .enderecoEntrega(pedido.getEnderecoEntrega())
                .rua(pedido.getRua())
                .numero(pedido.getNumero())
                .bairro(pedido.getBairro())
                .cidade(pedido.getCidade())
                .estado(pedido.getEstado())
                .cep(pedido.getCep())
                .build();
    }
}
