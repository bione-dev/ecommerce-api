package com.bione.api.ecommerce.dto;

import com.bione.api.ecommerce.model.Cliente;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String estado;
    private String cidade;
    private String bairro;
    private String complemento;
    private String rua;
    private String cep;
    private String numero;

    private String senha; // ✅ Adicionando o campo senha

    // ✅ Construtor que recebe um Cliente e preenche o DTO
    public ClienteDTO(Cliente cliente) {
        this.id = cliente.getId();
        this.nome = cliente.getNome();
        this.email = cliente.getEmail();
        this.telefone = cliente.getTelefone();
        this.estado = cliente.getEstado();
        this.cidade = cliente.getCidade();
        this.bairro = cliente.getBairro();
        this.complemento = cliente.getComplemento();
        this.rua = cliente.getRua();
        this.cep = cliente.getCep();
        this.numero = cliente.getNumero();
        this.senha = null; // Senha não deve ser retornada por segurança
    }
}
