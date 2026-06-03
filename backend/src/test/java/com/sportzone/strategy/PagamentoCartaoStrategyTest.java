package com.sportzone.strategy;

import com.sportzone.model.Pagamento;
import com.sportzone.model.enums.MetodoPagamento;
import com.sportzone.model.enums.StatusPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoCartaoStrategyTest {

    private final PagamentoCartaoStrategy strategy = new PagamentoCartaoStrategy();

    @Test
    void getMetodo_DeveRetornarCartaoCredito() {
        assertEquals(MetodoPagamento.CARTAO_CREDITO, strategy.getMetodo());
    }

    @Test
    void processar_DeveRetornarPagamentoAprovado() {
        BigDecimal valor = new BigDecimal("250.75");
        Pagamento pagamento = strategy.processar(valor);

        assertNotNull(pagamento);
        assertEquals(MetodoPagamento.CARTAO_CREDITO, pagamento.getMetodo());
        assertEquals(StatusPagamento.APROVADO, pagamento.getStatus());
        assertEquals(valor, pagamento.getValor());
        assertNotNull(pagamento.getTransacaoId());
        assertTrue(pagamento.getTransacaoId().startsWith("CARD-"));
    }
}
