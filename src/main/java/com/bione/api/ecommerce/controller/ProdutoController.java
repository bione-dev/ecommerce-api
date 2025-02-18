package com.bione.api.ecommerce.controller;

import com.bione.api.ecommerce.dto.ProdutoDTO;
import com.bione.api.ecommerce.service.ProdutoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@Tag(name = "Produtos", description = "Gerenciamento de produtos")
@RestController
@RequestMapping("/api/produtos")
@Validated
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    // ✅ Listar todos os produtos
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listarProdutos() {
        List<ProdutoDTO> produtos = produtoService.listarProdutos();
        return produtos.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(produtos);
    }

    // ✅ Buscar um produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarProduto(@PathVariable Long id) {
        try {
            ProdutoDTO produto = produtoService.buscarProdutoPorId(id);
            return ResponseEntity.ok(produto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Criar um novo produto
    @PostMapping
    public ResponseEntity<ProdutoDTO> criarProduto(@Valid @RequestBody ProdutoDTO produtoDTO) {
        try {
            ProdutoDTO produtoSalvo = produtoService.salvarProduto(produtoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ✅ Atualizar um produto existente
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizarProduto(@PathVariable Long id,
                                                       @Valid @RequestBody ProdutoDTO produtoDTO) {
        try {
            ProdutoDTO produtoAtualizado = produtoService.atualizarProduto(id, produtoDTO);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Deletar um produto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        try {
            produtoService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
