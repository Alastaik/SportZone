package com.sportzone.strategy;

import com.sportzone.model.Pagamento;
import com.sportzone.model.enums.MetodoPagamento;
import com.sportzone.model.enums.StatusPagamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

// Estratégia de pagamento via Cartão de Crédito
@Slf4j
@Component
public class PagamentoCartaoStrategy implements PagamentoStrategy {

    @Override
    public MetodoPagamento getMetodo() {
        return MetodoPagamento.CARTAO_CREDITO;
    }

    @Override
    public Pagamento processar(BigDecimal valor) {
        log.info("[CARTÃO] Processando pagamento de R$ {} via cartão de crédito...", valor);

        // Simulação de gateway — em produção usaria Stripe, PagSeguro, etc.
        String transacaoId = "CARD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.info("[CARTÃO] Pagamento aprovado. Transação: {}", transacaoId);

        return Pagamento.builder()
                .metodo(MetodoPagamento.CARTAO_CREDITO)
                .status(StatusPagamento.APROVADO)
                .valor(valor)
                .transacaoId(transacaoId)
                .build();
    }
}
