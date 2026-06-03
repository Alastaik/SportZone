package com.sportzone.controller;

import com.sportzone.model.Produto;
import com.sportzone.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// Controller REST para consulta do catálogo de produtos
@Slf4j
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    // GET /api/produtos — Lista todos os produtos do catálogo
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        log.info("GET /api/produtos — Listando catálogo");
        List<Produto> produtos = produtoRepository.findAll();
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/{id} — Busca um produto específico
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable UUID id) {
        log.info("GET /api/produtos/{} — Consulta de produto", id);
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
