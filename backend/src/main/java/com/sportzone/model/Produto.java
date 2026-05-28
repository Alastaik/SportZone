package com.sportzone.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

// Produto do catálogo esportivo
@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    // Descrição longa do produto
    @Column(columnDefinition = "TEXT")
    private String descricao;

    // Preço de venda atual
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private String categoria;

    private String marca;

    // Quantidade disponível no estoque
    @Column(nullable = false)
    private Integer quantidadeEstoque;

    // Ajusta o estoque (positivo = entrada, negativo = saída)
    public void atualizarEstoque(int quantidade) {
        this.quantidadeEstoque += quantidade;
    }
}
