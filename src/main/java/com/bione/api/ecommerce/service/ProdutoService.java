package com.bione.api.ecommerce.service;

import com.bione.api.ecommerce.dto.ProdutoDTO;
import com.bione.api.ecommerce.exception.ResourceNotFoundException;
import com.bione.api.ecommerce.model.Produto;
import com.bione.api.ecommerce.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j  // Adiciona logs para monitoramento
@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    /**
     * Lista todos os produtos disponíveis
     * @return Lista de ProdutoDTO
     */
    public List<ProdutoDTO> listarProdutos() {
        log.info("Listando todos os produtos disponíveis...");
        return produtoRepository.findAll()
                .stream()
                .map(produto -> new ProdutoDTO(
                        produto.getId(),
                        produto.getNome(),
                        produto.getPreco(),
                        produto.getEstoque()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Busca um produto pelo ID
     * @param id ID do produto
     * @return ProdutoDTO correspondente
     * @throws ResourceNotFoundException Se o produto não for encontrado
     */
    public ProdutoDTO buscarProdutoPorId(Long id) {
        log.info("Buscando produto com ID: {}", id);
        return produtoRepository.findById(id)
                .map(produto -> new ProdutoDTO(
                        produto.getId(),
                        produto.getNome(),
                        produto.getPreco(),
                        produto.getEstoque()
                ))
                .orElseThrow(() -> {
                    log.warn("Produto com ID {} não encontrado", id);
                    return new ResourceNotFoundException("Produto não encontrado");
                });
    }

    /**
     * Salva um novo produto no banco de dados
     * @param produtoDTO Produto a ser salvo
     * @return ProdutoDTO salvo
     */
    public ProdutoDTO salvarProduto(ProdutoDTO produtoDTO) {
        log.info("Salvando novo produto: {}", produtoDTO.getNome());

        Produto produto = new Produto();
        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());
        produto.setEstoque(produtoDTO.getEstoque());

        Produto salvo = produtoRepository.save(produto);
        log.info("Produto salvo com sucesso! ID: {}", salvo.getId());

        return new ProdutoDTO(salvo.getId(), salvo.getNome(), salvo.getPreco(), salvo.getEstoque());
    }

    /**
     * Atualiza um produto existente
     * @param id ID do produto a ser atualizado
     * @param produtoDTO Novos dados do produto
     * @return ProdutoDTO atualizado
     * @throws ResourceNotFoundException Se o produto não for encontrado
     */
    public ProdutoDTO atualizarProduto(Long id, ProdutoDTO produtoDTO) {
        log.info("Atualizando produto com ID: {}", id);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto com ID {} não encontrado para atualização", id);
                    return new ResourceNotFoundException("Produto não encontrado");
                });

        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());
        produto.setEstoque(produtoDTO.getEstoque());

        Produto atualizado = produtoRepository.save(produto);
        log.info("Produto atualizado com sucesso! ID: {}", atualizado.getId());

        return new ProdutoDTO(atualizado.getId(), atualizado.getNome(), atualizado.getPreco(), atualizado.getEstoque());
    }

    /**
     * Deleta um produto pelo ID
     * @param id ID do produto a ser deletado
     * @throws ResourceNotFoundException Se o produto não for encontrado
     */
    public void deletarProduto(Long id) {
        log.info("Deletando produto com ID: {}", id);
        if (!produtoRepository.existsById(id)) {
            log.warn("Produto com ID {} não encontrado para exclusão", id);
            throw new ResourceNotFoundException("Produto não encontrado");
        }
        produtoRepository.deleteById(id);
        log.info("Produto com ID {} deletado com sucesso!", id);
    }
}
