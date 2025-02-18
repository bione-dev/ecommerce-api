package com.bione.api.ecommerce.controller;

import com.bione.api.ecommerce.dto.ClienteDTO;
import com.bione.api.ecommerce.exception.ClienteNotFoundException;
import com.bione.api.ecommerce.exception.ClienteInvalidException;
import com.bione.api.ecommerce.exception.ErroPadraoDTO;
import com.bione.api.ecommerce.service.ClienteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j // Adiciona logs para facilitar debug
@CrossOrigin("*")
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
@RestController
@RequestMapping("/api/clientes")
@Validated
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Lista todos os clientes.
     */
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        log.info("Listando todos os clientes...");
        List<ClienteDTO> clientes = clienteService.listarClientes();
        return clientes.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(clientes);
    }

    /**
     * Busca um cliente por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarClientePorId(@PathVariable Long id) {
        log.info("Buscando cliente com ID: {}", id);
        ClienteDTO clienteDTO = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(clienteDTO);
    }

    /**
     * Salva um novo cliente.
     */
    @PostMapping
    public ResponseEntity<ClienteDTO> salvarCliente(@RequestBody @Valid ClienteDTO clienteDTO) {
        log.info("Salvando novo cliente: {}", clienteDTO.getNome());
        ClienteDTO savedCliente = clienteService.salvarCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCliente);
    }

    /**
     * Atualiza um cliente existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizarCliente(@PathVariable Long id, @RequestBody @Valid ClienteDTO clienteDTO) {
        log.info("Atualizando cliente com ID: {}", id);

        if (clienteDTO.getSenha() != null && clienteDTO.getSenha().isBlank()) {
            clienteDTO.setSenha(null);
        }

        ClienteDTO updatedCliente = clienteService.atualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(updatedCliente);
    }

    /**
     * Exclui um cliente pelo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        log.info("Deletando cliente com ID: {}", id);
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Exporta clientes para CSV.
     */
    @GetMapping("/exportar/csv")
    public void exportarClientesParaCSV(HttpServletResponse response) throws IOException {
        log.info("Exportando clientes para CSV...");
        List<ClienteDTO> clientes = clienteService.listarClientes();
        clienteService.exportarClientesParaCSV(clientes, response);
    }

    /**
     * Tratamento para ClienteNotFoundException.
     */
    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ErroPadraoDTO> handleClienteNotFoundException(ClienteNotFoundException ex) {
        log.warn("Erro: Cliente não encontrado - ID: {}", ex.getDetails());
        ErroPadraoDTO erro = new ErroPadraoDTO(
                HttpStatus.NOT_FOUND.value(),
                "Cliente não encontrado",
                "Verifique se o ID (" + ex.getDetails() + ") é válido."
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    /**
     * Tratamento para ClienteInvalidException.
     */
    @ExceptionHandler(ClienteInvalidException.class)
    public ResponseEntity<ErroPadraoDTO> handleClienteInvalidException(ClienteInvalidException ex) {
        log.warn("Erro de validação: {}", ex.getMessage());
        ErroPadraoDTO erro = new ErroPadraoDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /**
     * Tratamento genérico de erros.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroPadraoDTO> handleGenericException(Exception ex) {
        log.error("Erro interno no servidor: {}", ex.getMessage());
        ErroPadraoDTO erro = new ErroPadraoDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno no servidor",
                "Entre em contato com o suporte técnico."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
