package com.bione.api.ecommerce.repository;

import com.bione.api.ecommerce.model.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {

    // Busca histórico de status de um pedido específico, ordenado da mudança mais recente para a mais antiga
    @Transactional(readOnly = true)
    List<StatusHistory> findByPedidoIdOrderByDataAlteracaoDesc(Long pedidoId);

    // Busca histórico de status entre duas datas (qualquer pedido)
    @Transactional(readOnly = true)
    List<StatusHistory> findByDataAlteracaoBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Busca histórico de um pedido específico dentro de um intervalo de tempo
    @Transactional(readOnly = true)
    @Query("SELECT sh FROM StatusHistory sh WHERE sh.pedido.id = :pedidoId AND sh.dataAlteracao BETWEEN :startDate AND :endDate ORDER BY sh.dataAlteracao DESC")
    List<StatusHistory> findByPedidoIdAndDataAlteracaoBetween(Long pedidoId, LocalDateTime startDate, LocalDateTime endDate);
}
