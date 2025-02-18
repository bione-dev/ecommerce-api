package com.bione.api.ecommerce;

import com.bione.api.ecommerce.controller.ClienteController;
import com.bione.api.ecommerce.dto.ClienteDTO;
import com.bione.api.ecommerce.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)  // Carrega apenas o Controller para os testes
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteService clienteService;

    // Exemplo de ClienteDTO válido
    private ClienteDTO clienteDTO;

    @BeforeEach
    public void setup() {
        clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Gustavo Bione");
        clienteDTO.setEmail("gustavo.bione@gmail.com");
        clienteDTO.setTelefone("(11) 98765-4321");
        clienteDTO.setEstado("SP");
        clienteDTO.setCidade("São Paulo");
        clienteDTO.setBairro("Centro");
        clienteDTO.setRua("Rua das Flores");  // Alterado para "rua"
        clienteDTO.setCep("01000-000");
        clienteDTO.setNumero("123");
        clienteDTO.setSenha("Senha123!");
    }

    @Test
    public void testSalvarClienteValido() throws Exception {
        // Testando POST para salvar um cliente válido
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is(clienteDTO.getNome())))
                .andExpect(jsonPath("$.email", is(clienteDTO.getEmail())))
                .andExpect(jsonPath("$.telefone", is(clienteDTO.getTelefone())))
                .andExpect(jsonPath("$.estado", is(clienteDTO.getEstado())))
                .andExpect(jsonPath("$.cidade", is(clienteDTO.getCidade())))
                .andExpect(jsonPath("$.bairro", is(clienteDTO.getBairro())))
                .andExpect(jsonPath("$.cep", is(clienteDTO.getCep())))
                .andExpect(jsonPath("$.numero", is(clienteDTO.getNumero())))
                .andExpect(jsonPath("$.senha", is(clienteDTO.getSenha())))
                .andExpect(jsonPath("$.rua", is(clienteDTO.getRua())));  // Verificação para "rua"
    }

    @Test
    public void testSalvarClienteEmailInvalido() throws Exception {
        // Testando POST com email inválido
        clienteDTO.setEmail("invalidemail");
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Email inválido")));
    }

    @Test
    public void testAtualizarClienteValido() throws Exception {
        // Testando PUT para atualizar um cliente válido
        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(clienteDTO.getNome())))
                .andExpect(jsonPath("$.email", is(clienteDTO.getEmail())))
                .andExpect(jsonPath("$.telefone", is(clienteDTO.getTelefone())))
                .andExpect(jsonPath("$.estado", is(clienteDTO.getEstado())))
                .andExpect(jsonPath("$.cidade", is(clienteDTO.getCidade())))
                .andExpect(jsonPath("$.bairro", is(clienteDTO.getBairro())))
                .andExpect(jsonPath("$.cep", is(clienteDTO.getCep())))
                .andExpect(jsonPath("$.numero", is(clienteDTO.getNumero())))
                .andExpect(jsonPath("$.senha", is(clienteDTO.getSenha())))
                .andExpect(jsonPath("$.rua", is(clienteDTO.getRua())));  // Verificação para "rua"
    }

    @Test
    public void testBuscarClientePorId() throws Exception {
        // Testando GET para buscar cliente por ID
        mockMvc.perform(get("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Gustavo Bione")))
                .andExpect(jsonPath("$.email", is("gustavo.bione@gmail.com")));
    }

    @Test
    public void testBuscarClientePorIdNaoEncontrado() throws Exception {
        // Testando GET com ID não encontrado
        mockMvc.perform(get("/api/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeletarCliente() throws Exception {
        // Testando DELETE para deletar cliente
        mockMvc.perform(delete("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeletarClienteNaoEncontrado() throws Exception {
        // Testando DELETE com ID não encontrado
        mockMvc.perform(delete("/api/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
