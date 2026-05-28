package com.sportzone.strategy;

import com.sportzone.model.enums.MetodoPagamento;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// Factory que resolve a Strategy correta a partir do MetodoPagamento
// Usa getMetodo() de cada Strategy para auto-registro (Open/Closed Principle)
@Component
public class PagamentoStrategyFactory {

    private final Map<MetodoPagamento, PagamentoStrategy> strategies;

    // O Spring injeta automaticamente todas as implementações de PagamentoStrategy
    public PagamentoStrategyFactory(List<PagamentoStrategy> strategyList) {
        this.strategies = new EnumMap<>(MetodoPagamento.class);

        // Registra cada strategy pelo método que ela suporta
        for (PagamentoStrategy strategy : strategyList) {
            strategies.put(strategy.getMetodo(), strategy);
        }
    }

    // Retorna a strategy correspondente ao método escolhido pelo cliente
    public PagamentoStrategy getStrategy(MetodoPagamento metodo) {
        PagamentoStrategy strategy = strategies.get(metodo);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Método de pagamento não suportado: " + metodo
            );
        }
        return strategy;
    }
}
