package com.sportzone.model;

import com.sportzone.model.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Agregado raiz do checkout — contém itens e pagamento
@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Data de criação do pedido
    @Column(nullable = false)
    private LocalDateTime dataPedido;

    // Status atual do pedido (PENDENTE, CONFIRMADO, CANCELADO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    // Valor total calculado a partir dos itens
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    // Relacionamento 1:N — Um pedido contém muitos itens
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPedido> itens = new ArrayList<>();

    // Relacionamento 1:1 — Um pedido possui um pagamento
    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pagamento pagamento;

    // Soma o subtotal de cada item para obter o total do pedido
    public void calcularTotal() {
        this.valorTotal = this.itens.stream()
                .map(ItemPedido::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Marca o pedido como confirmado
    public void confirmar() {
        this.status = StatusPedido.CONFIRMADO;
    }

    // Marca o pedido como cancelado
    public void cancelar() {
        this.status = StatusPedido.CANCELADO;
    }

    // Adiciona um item e mantém a referência bidirecional
    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
        item.setPedido(this);
    }

    // Vincula o pagamento e mantém a referência bidirecional
    public void definirPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
        pagamento.setPedido(this);
    }
}
