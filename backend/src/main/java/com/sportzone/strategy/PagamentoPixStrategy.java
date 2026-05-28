package com.sportzone.strategy;

import com.sportzone.model.Pagamento;
import com.sportzone.model.enums.MetodoPagamento;
import com.sportzone.model.enums.StatusPagamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

// Estratégia de pagamento via Pix (confirmação instantânea)
@Slf4j
@Component
public class PagamentoPixStrategy implements PagamentoStrategy {

    @Override
    public MetodoPagamento getMetodo() {
        return MetodoPagamento.PIX;
    }

    @Override
    public Pagamento processar(BigDecimal valor) {
        log.info("[PIX] Gerando código Pix para pagamento de R$ {}...", valor);

        // Simulação de API Pix — em produção usaria PSP do Banco Central
        String transacaoId = "PIX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.info("[PIX] Pagamento confirmado instantaneamente. Transação: {}", transacaoId);

        return Pagamento.builder()
                .metodo(MetodoPagamento.PIX)
                .status(StatusPagamento.APROVADO)
                .valor(valor)
                .transacaoId(transacaoId)
                .build();
    }
}
