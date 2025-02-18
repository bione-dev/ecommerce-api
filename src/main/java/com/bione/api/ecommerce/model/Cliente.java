package com.bione.api.ecommerce.model;

import com.bione.api.ecommerce.dto.ClienteDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "senha") // Evita expor a senha em logs automaticamente
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false) // Garante que emails são únicos e não nulos
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String bairro;

    private String complemento;

    @Column(nullable = false)
    private String rua;  // Correção feita para usar "rua"

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String senha; // Deve ser armazenada criptografada

    /**
     * Método para obter o endereço completo do cliente.
     * Evita `NullPointerException` verificando valores nulos.
     */
    public String getEnderecoCompleto() {
        return String.format("%s, %s, %s, %s, %s, %s",
                rua != null ? rua : "",
                numero != null ? numero : "",
                bairro != null ? bairro : "",
                cidade != null ? cidade : "",
                estado != null ? estado : "",
                cep != null ? cep : "").trim();
    }

    /**
     * Construtor que converte um `ClienteDTO` em um `Cliente`.
     */
    public Cliente(ClienteDTO clienteDTO) {
        this.id = clienteDTO.getId();
        this.nome = clienteDTO.getNome();
        this.email = clienteDTO.getEmail();
        this.telefone = clienteDTO.getTelefone();
        this.estado = clienteDTO.getEstado();
        this.cidade = clienteDTO.getCidade();
        this.bairro = clienteDTO.getBairro();
        this.complemento = clienteDTO.getComplemento();
        this.rua = clienteDTO.getRua();
        this.cep = clienteDTO.getCep();
        this.numero = clienteDTO.getNumero();
        this.senha = clienteDTO.getSenha();
    }

    /**
     * Método que pode ser usado para formatar ou validar dados antes de salvar.
     */
    @PrePersist
    @PreUpdate
    private void formatarDados() {
        if (this.email != null) {
            this.email = this.email.toLowerCase().trim(); // Armazena emails em minúsculas e sem espaços extras
        }
        if (this.telefone != null) {
            this.telefone = this.telefone.replaceAll("[^0-9]", ""); // Remove caracteres não numéricos
        }
    }
}
