package com.bione.api.ecommerce.model;

import com.bione.api.ecommerce.dto.ProdutoDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pedido_produto")
public class PedidoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    private Integer quantidade;

    // ✅ Construtor necessário para corrigir o erro
    public PedidoProduto(Pedido pedido, Produto produto, Integer quantidade) {
        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    // ✅ Método auxiliar para converter PedidoProduto em ProdutoDTO
    public ProdutoDTO toProdutoDTO() {
        return new ProdutoDTO(
                this.produto.getId(),
                this.produto.getNome(),
                this.produto.getPreco(),
                this.produto.getEstoque()
                // Adicionando quantidade ao DTO
        );
    }
}
