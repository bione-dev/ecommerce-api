package com.bione.api.ecommerce.controller;

import com.bione.api.ecommerce.dto.PedidoDTO;
import com.bione.api.ecommerce.dto.PedidoRequestDTO;
import com.bione.api.ecommerce.dto.StatusHistoryDTO;
import com.bione.api.ecommerce.exception.ResourceNotFoundException;
import com.bione.api.ecommerce.service.PedidoService;
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

@Slf4j
@CrossOrigin("*")
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos")
@RestController
@RequestMapping("/api/pedidos")
@Validated
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * Lista todos os pedidos.
     */
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        log.info("Listando todos os pedidos...");
        List<PedidoDTO> pedidos = pedidoService.listarPedidos(); // ✅ Método corrigido
        return pedidos.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(pedidos);
    }


    /**
     * Cria um novo pedido.
     */
    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        log.info("Criando novo pedido para o cliente ID: {}", pedidoRequestDTO.getClienteId());
        try {
            PedidoDTO novoPedido = pedidoService.criarPedido(pedidoRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
        } catch (Exception e) {
            log.error("Erro ao criar pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Atualiza o status de um pedido.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoDTO> atualizarStatus(@PathVariable Long id, @RequestBody StatusHistoryDTO statusDTO) {
        log.info("Atualizando status do pedido ID: {} para {}", id, statusDTO.getStatus());
        try {
            PedidoDTO pedidoDTO = pedidoService.atualizarStatusPedido(id, statusDTO); // Usando o StatusHistoryDTO
            return ResponseEntity.ok(pedidoDTO);
        } catch (ResourceNotFoundException e) {
            log.error("Pedido não encontrado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Retorna 404 se o pedido não for encontrado
        } catch (IllegalArgumentException e) {
            log.error("Erro ao atualizar status, status inválido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Retorna 400 para status inválido
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar status do pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Retorna 500 para erro inesperado
        }
    }

    /**
     * Lista pedidos por status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PedidoDTO>> listarPedidosPorStatus(@PathVariable String status) {
        log.info("Listando pedidos com status: {}", status);
        try {
            List<PedidoDTO> pedidos = pedidoService.listarPedidosPorStatus(status); // ✅ Método corrigido
            return pedidos.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            log.error("Erro ao listar pedidos por status '{}': {}", status, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Retorna 500 em caso de erro
        }
    }

    /**
     * Atualiza o número de rastreamento de um pedido.
     */
    @PutMapping("/{id}/rastreamento")
    public ResponseEntity<PedidoDTO> atualizarRastreamento(@PathVariable Long id, @RequestBody String numeroRastreamento) {
        log.info("Atualizando rastreamento do pedido ID: {} para {}", id, numeroRastreamento);
        try {
            PedidoDTO pedidoDTO = pedidoService.atualizarRastreamentoPedido(id, numeroRastreamento); // ✅ Método corrigido
            return ResponseEntity.ok(pedidoDTO);
        } catch (Exception e) {
            log.error("Erro ao atualizar rastreamento do pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Retorna 400 em caso de erro
        }
    }

    /**
     * Exporta pedidos para CSV.
     */
    @GetMapping("/exportar/csv")
    public void exportarPedidosParaCSV(HttpServletResponse response) throws IOException {
        log.info("Exportando pedidos para CSV...");
        try {
            List<PedidoDTO> pedidos = pedidoService.listarPedidosPorStatus("EM_ANDAMENTO"); // ✅ Método corrigido
            pedidoService.exportarPedidosParaCSV(pedidos, response);
        } catch (IOException e) {
            log.error("Erro ao exportar os pedidos para CSV: {}", e.getMessage(), e);
            throw new IOException("Erro ao exportar os pedidos para CSV.", e); // Retorna o erro de I/O
        }
    }
}
