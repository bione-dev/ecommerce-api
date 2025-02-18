package com.bione.api.ecommerce.repository;

import com.bione.api.ecommerce.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Consulta para buscar pedidos com o status e seus itens carregados
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itensPedido WHERE (:status IS NULL OR p.status = :status)")
    List<Pedido> findAllWithItens(@Param("status") String status);


    // Consulta simples para buscar pedidos pelo status
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itensPedido WHERE p.status = :status")
    List<Pedido> findByStatusWithItens(String status);
}
