package com.sportzone.model;

import com.sportzone.model.enums.MetodoPagamento;
import com.sportzone.model.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

// Pagamento vinculado a um pedido — preenchido pela Strategy
@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Método utilizado (CARTAO_CREDITO ou PIX)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPagamento metodo;

    // Resultado do processamento (PENDENTE, APROVADO, RECUSADO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;

    // Valor cobrado
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    // ID gerado pelo gateway/simulação para rastreio
    @Column(unique = true)
    private String transacaoId;

    // Relacionamento 1:1 — Pagamento pertence a um pedido
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // Marca o pagamento como estornado
    public void estornar() {
        this.status = StatusPagamento.RECUSADO;
    }
}
