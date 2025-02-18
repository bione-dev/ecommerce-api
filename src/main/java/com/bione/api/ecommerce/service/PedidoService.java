package com.bione.api.ecommerce.service;

import com.bione.api.ecommerce.dto.*;
import com.bione.api.ecommerce.enums.StatusPedido;
import com.bione.api.ecommerce.exception.ResourceNotFoundException;
import com.bione.api.ecommerce.model.*;
import com.bione.api.ecommerce.repository.*;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final PedidoProdutoRepository pedidoProdutoRepository;

    // Método para mapear produtos
    private List<ProdutoDTO> mapearProdutosParaDTO(List<PedidoProduto> itensPedido) {
        return itensPedido.stream()
                .map(item -> new ProdutoDTO(
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getProduto().getPreco(),
                        item.getProduto().getEstoque()
                ))
                .collect(Collectors.toList());
    }


    public List<PedidoDTO> listarPedidos() {
        log.info("Listando todos os pedidos...");
        try {
            // Passando o parâmetro status para a consulta
            List<PedidoDTO> pedidos = pedidoRepository.findAllWithItens(null)  // Modificar conforme necessário
                    .stream()
                    .map(PedidoDTO::fromEntity)
                    .collect(Collectors.toList());

            if (pedidos.isEmpty()) {
                log.warn("Nenhum pedido encontrado.");
            }

            return pedidos;
        } catch (Exception e) {
            log.error("Erro ao listar pedidos: {}", e.getMessage(), e);
            throw new ResourceNotFoundException("Erro ao listar pedidos", e);
        }
    }








    public List<PedidoDTO> listarPedidosPorStatus(String status) {
        log.info("Listando pedidos com status: {}", status);
        try {
            // Usando o método findByStatusWithItens para garantir que os itens sejam carregados junto com o pedido
            List<PedidoDTO> pedidos = pedidoRepository.findByStatusWithItens(status)
                    .stream()
                    .map(PedidoDTO::fromEntity)
                    .collect(Collectors.toList());

            if (pedidos.isEmpty()) {
                log.warn("Nenhum pedido encontrado com status {}", status);
            }

            return pedidos;
        } catch (Exception e) {
            log.error("Erro ao listar pedidos por status '{}': {}", status, e.getMessage(), e);
            throw new ResourceNotFoundException("Erro ao listar pedidos com status: " + status, e);
        }
    }



    // Atualiza o rastreamento de um pedido
    @Transactional
    public PedidoDTO atualizarRastreamentoPedido(Long id, String numeroRastreamento) {
        log.info("Atualizando rastreamento do pedido ID: {}", id);
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

            pedido.setNumeroRastreamento(numeroRastreamento);
            pedidoRepository.save(pedido);
            return PedidoDTO.fromEntity(pedido);
        } catch (Exception e) {
            log.error("Erro ao atualizar rastreamento do pedido ID {}: {}", id, e.getMessage(), e);
            throw new ResourceNotFoundException("Erro ao atualizar rastreamento do pedido com ID: " + id, e);
        }
    }

    // Cria um novo pedido
    @Transactional
    public PedidoDTO criarPedido(@Valid PedidoRequestDTO pedidoRequestDTO) {
        log.info("Criando pedido para Cliente ID: {}", pedidoRequestDTO.getClienteId());

        try {
            Cliente cliente = clienteRepository.findById(pedidoRequestDTO.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

            List<Long> idsProdutos = pedidoRequestDTO.getProdutos().stream()
                    .map(ProdutoQuantidadeDTO::getProdutoId)
                    .collect(Collectors.toList());

            List<Produto> produtos = produtoRepository.findAllById(idsProdutos);

            if (produtos.size() != idsProdutos.size()) {
                throw new ResourceNotFoundException("Um ou mais produtos não foram encontrados");
            }

            AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);

            List<PedidoProduto> itensPedido = produtos.stream()
                    .map(produto -> {
                        ProdutoQuantidadeDTO produtoQuantificado = pedidoRequestDTO.getProdutos().stream()
                                .filter(p -> p.getProdutoId().equals(produto.getId()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado na lista"));

                        Integer quantidade = produtoQuantificado.getQuantidade();

                        if (produto.getEstoque() < quantidade) {
                            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
                        }

                        produto.setEstoque(produto.getEstoque() - quantidade);
                        produtoRepository.save(produto);

                        BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
                        total.updateAndGet(t -> t.add(subtotal));

                        return new PedidoProduto(null, produto, quantidade);
                    })
                    .collect(Collectors.toList());

            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setTotal(total.get());
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setStatus(StatusPedido.EM_ANDAMENTO.name());  // Alteração para usar o enum
            pedido.setEnderecoEntrega(cliente.getEnderecoCompleto());
            pedido.setNumeroRastreamento(generateTrackingCode());

            Pedido salvo = pedidoRepository.save(pedido);

            itensPedido.forEach(item -> item.setPedido(salvo));

            pedidoProdutoRepository.saveAll(itensPedido);

            List<ProdutoDTO> produtoDTOs = mapearProdutosParaDTO(itensPedido);

            log.info("Pedido ID {} criado com sucesso", salvo.getId());

            return PedidoDTO.builder()
                    .id(salvo.getId())
                    .clienteId(salvo.getCliente().getId())
                    .produtos(produtoDTOs)
                    .total(salvo.getTotal())
                    .dataPedido(salvo.getDataPedido())
                    .status(StatusPedido.valueOf(salvo.getStatus()))  // Já está correto com enum
                    .enderecoEntrega(salvo.getEnderecoEntrega())
                    .rua(salvo.getRua())
                    .numero(salvo.getNumero())
                    .bairro(salvo.getBairro())
                    .cidade(salvo.getCidade())
                    .estado(salvo.getEstado())
                    .cep(salvo.getCep())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao criar pedido: {}", e.getMessage(), e);
            throw new ResourceNotFoundException("Erro ao criar pedido", e);
        }
    }

    // Atualiza o status de um pedido
    @Transactional
    public PedidoDTO atualizarStatusPedido(Long id, StatusHistoryDTO statusDTO) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        try {
            // Verifica se o status enviado é válido
            StatusPedido statusEnum = statusDTO.getStatus();
            if (statusEnum == null) {
                throw new IllegalArgumentException("Status inválido: " + statusDTO.getStatus());
            }

            // Atualiza o status do pedido
            pedido.setStatus(statusEnum.name()); // Atualiza o status no pedido

            // Salva o pedido atualizado
            pedidoRepository.save(pedido);

            // Registra o histórico de status
            StatusHistory statusHistory = new StatusHistory();
            statusHistory.setPedido(pedido);
            statusHistory.setStatus(statusEnum); // Usando o status como enum
            statusHistory.setDataAlteracao(LocalDateTime.now());
            statusHistoryRepository.save(statusHistory);

            return PedidoDTO.fromEntity(pedido);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao atualizar status do pedido ID {}: {}", id, e.getMessage(), e);
            throw new IllegalArgumentException("Status inválido: " + statusDTO.getStatus(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar status do pedido ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar status do pedido", e);
        }
    }

    // Gera código único para rastreamento
    public String generateTrackingCode() {
        return UUID.randomUUID().toString();
    }

    // Exporta pedidos para CSV
    public void exportarPedidosParaCSV(List<PedidoDTO> pedidos, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=pedidos.csv");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(new String[]{"ID", "Cliente", "Status", "Total", "Data Pedido", "Endereço"});

            for (PedidoDTO pedido : pedidos) {
                writer.writeNext(new String[] {
                        String.valueOf(pedido.getId()),
                        String.valueOf(pedido.getClienteId()),
                        pedido.getStatus().toString(),  // Usa toString() para o enum
                        pedido.getTotal().toString(),
                        pedido.getDataPedido().toString(),
                        pedido.getRua() + ", " + pedido.getNumero() + ", " + pedido.getBairro() + ", " +
                                pedido.getCidade() + ", " + pedido.getEstado() + ", " + pedido.getCep()
                });
            }
        } catch (IOException e) {
            log.error("Erro ao exportar os pedidos para CSV: {}", e.getMessage());
            throw new IOException("Erro ao exportar os pedidos para CSV.", e);
        }
    }
}
