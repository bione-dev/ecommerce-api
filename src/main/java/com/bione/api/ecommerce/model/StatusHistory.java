package com.bione.api.ecommerce.model;

import com.bione.api.ecommerce.enums.StatusPedido;  // Importando StatusPedido do pacote correto

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;  // Status do pedido agora usa ENUM

    @Column(name = "data_alteracao", nullable = false)
    private LocalDateTime dataAlteracao;  // Data e hora da mudança de status

    @PrePersist
    @PreUpdate
    public void atualizarDataAlteracao() {
        dataAlteracao = LocalDateTime.now();  // Atualiza a data automaticamente
    }
}
