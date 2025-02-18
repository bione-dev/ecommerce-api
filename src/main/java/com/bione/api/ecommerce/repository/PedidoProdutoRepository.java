package com.bione.api.ecommerce.repository;

import com.bione.api.ecommerce.model.PedidoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoProdutoRepository extends JpaRepository<PedidoProduto, Long> {
}
