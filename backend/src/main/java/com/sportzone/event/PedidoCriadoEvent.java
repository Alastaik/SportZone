package com.sportzone.event;

import com.sportzone.model.enums.MetodoPagamento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

// Evento publicado no Kafka quando um pedido é criado
// Contém apenas os dados necessários para o Worker processar
public record PedidoCriadoEvent(
        UUID pedidoId,              // ID do pedido salvo no banco
        BigDecimal valorTotal,      // Valor total calculado
        MetodoPagamento metodoPagamento  // Método de pagamento escolhido
) implements Serializable {
}
