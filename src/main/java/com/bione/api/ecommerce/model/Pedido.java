package com.bione.api.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Relacionamento correto com os produtos no pedido (tabela intermediária)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PedidoProduto> itensPedido;  // Lista dos produtos e suas quantidades

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    private LocalDateTime dataPedido;  // Data do pedido
    private String status;  // Status do pedido (ex: "Em andamento", "Finalizado")

    // Campos de endereço do pedido
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    @Column(name = "numero_rastreamento", unique = true)
    private String numeroRastreamento;  // Número de rastreamento

    /**
     * Método para obter o endereço completo.
     * Evita `NullPointerException` verificando valores nulos.
     */
    public String getEnderecoEntrega() {
        return String.format("%s, %s, %s, %s, %s, %s",
                rua != null ? rua : "",
                numero != null ? numero : "",
                bairro != null ? bairro : "",
                cidade != null ? cidade : "",
                estado != null ? estado : "",
                cep != null ? cep : "").trim();
    }

    /**
     * Define o endereço completo separando os campos corretamente.
     */
    public void setEnderecoEntrega(String enderecoCompleto) {
        if (enderecoCompleto != null) {
            String[] partes = enderecoCompleto.split(", ");
            if (partes.length >= 6) {
                this.rua = partes[0];
                this.numero = partes[1];
                this.bairro = partes[2];
                this.cidade = partes[3];
                this.estado = partes[4];
                this.cep = partes[5];
            }
        }
    }

    /**
     * Método para calcular o total do pedido com base nos produtos e quantidades.
     * Executado antes de salvar (`@PrePersist`) e antes de atualizar (`@PreUpdate`).
     */
    @PrePersist
    @PreUpdate
    public void calcularTotal() {
        if (itensPedido != null && !itensPedido.isEmpty()) {
            total = BigDecimal.ZERO;
            for (PedidoProduto item : itensPedido) {
                total = total.add(item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())));
            }
        }
    }
}
