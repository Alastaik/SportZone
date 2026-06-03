package com.sportzone.strategy;

import com.sportzone.model.enums.MetodoPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoStrategyFactoryTest {

    private PagamentoStrategyFactory factory;

    @BeforeEach
    void setUp() {
        // Criando instâncias reais para testar o auto-registro da Factory
        PagamentoPixStrategy pixStrategy = new PagamentoPixStrategy();
        PagamentoCartaoStrategy cartaoStrategy = new PagamentoCartaoStrategy();

        factory = new PagamentoStrategyFactory(List.of(pixStrategy, cartaoStrategy));
    }

    @Test
    void getStrategy_ComPix_DeveRetornarPagamentoPixStrategy() {
        PagamentoStrategy strategy = factory.getStrategy(MetodoPagamento.PIX);
        
        assertNotNull(strategy);
        assertTrue(strategy instanceof PagamentoPixStrategy);
    }

    @Test
    void getStrategy_ComCartao_DeveRetornarPagamentoCartaoStrategy() {
        PagamentoStrategy strategy = factory.getStrategy(MetodoPagamento.CARTAO_CREDITO);
        
        assertNotNull(strategy);
        assertTrue(strategy instanceof PagamentoCartaoStrategy);
    }

    @Test
    void getStrategy_ComMetodoNulo_DeveLancarException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.getStrategy(null);
        });

        assertTrue(exception.getMessage().contains("Método de pagamento não suportado"));
    }
}
