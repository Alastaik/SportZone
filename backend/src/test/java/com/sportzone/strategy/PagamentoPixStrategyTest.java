package com.sportzone.strategy;

import com.sportzone.model.Pagamento;
import com.sportzone.model.enums.MetodoPagamento;
import com.sportzone.model.enums.StatusPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoPixStrategyTest {

    private final PagamentoPixStrategy strategy = new PagamentoPixStrategy();

    @Test
    void getMetodo_DeveRetornarPix() {
        assertEquals(MetodoPagamento.PIX, strategy.getMetodo());
    }

    @Test
    void processar_DeveRetornarPagamentoAprovado() {
        BigDecimal valor = new BigDecimal("100.50");
        Pagamento pagamento = strategy.processar(valor);

        assertNotNull(pagamento);
        assertEquals(MetodoPagamento.PIX, pagamento.getMetodo());
        assertEquals(StatusPagamento.APROVADO, pagamento.getStatus());
        assertEquals(valor, pagamento.getValor());
        assertNotNull(pagamento.getTransacaoId());
        assertTrue(pagamento.getTransacaoId().startsWith("PIX-"));
    }
}
