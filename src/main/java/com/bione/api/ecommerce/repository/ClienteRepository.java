package com.bione.api.ecommerce.repository;

import com.bione.api.ecommerce.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca um cliente pelo e-mail
    @Query("SELECT c FROM Cliente c WHERE c.email = :email")
    Optional<Cliente> findByEmail(String email);

    // Verifica se um e-mail já está cadastrado (evita duplicação)
    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.email = :email")
    boolean existsByEmail(String email);

    // Verifica se um telefone já está cadastrado
    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.telefone = :telefone")
    boolean existsByTelefone(String telefone);
}
