package com.sportzone.strategy;

import com.sportzone.model.Pagamento;
import com.sportzone.model.enums.MetodoPagamento;

import java.math.BigDecimal;

// Interface Strategy — cada implementação processa um tipo de pagamento
public interface PagamentoStrategy {

    // Processa o pagamento e retorna a entidade com status e transacaoId preenchidos
    Pagamento processar(BigDecimal valor);

    // Identifica qual método de pagamento esta strategy suporta
    MetodoPagamento getMetodo();
}
