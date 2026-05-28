package com.sportzone.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

// Item de um pedido — vincula produto, quantidade e preço travado
@Entity
@Table(name = "itens_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Integer quantidade;

    // Preço capturado no momento da compra (não muda se o produto mudar)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    // Relacionamento N:1 — Vários itens pertencem a um pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // Relacionamento N:1 — Cada item referencia um produto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Calcula subtotal: precoUnitario × quantidade
    public BigDecimal calcularSubtotal() {
        return this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidade));
    }
}
