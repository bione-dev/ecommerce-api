package com.bione.api.ecommerce.repository;

import com.bione.api.ecommerce.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Busca produtos por nome ignorando maiúsculas/minúsculas
    @Transactional(readOnly = true)
    @Query("SELECT DISTINCT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Busca produtos que tenham estoque acima de determinado valor
    @Transactional(readOnly = true)
    List<Produto> findByEstoqueGreaterThan(Integer estoqueMinimo);

    // Busca produtos dentro de uma faixa de estoque (mínimo e máximo)
    @Transactional(readOnly = true)
    @Query("SELECT p FROM Produto p WHERE p.estoque BETWEEN :estoqueMin AND :estoqueMax")
    List<Produto> findByEstoqueBetween(Integer estoqueMin, Integer estoqueMax);

    // Busca produtos que estão sem estoque (estoque = 0)
    @Transactional(readOnly = true)
    @Query("SELECT p FROM Produto p WHERE p.estoque = 0")
    List<Produto> findProdutosEsgotados();
}
