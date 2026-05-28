package com.sportzone.model.enums;

// Representa os possíveis estados de um pedido no ciclo de vida
public enum StatusPedido {
    PENDENTE,    // Pedido criado, aguardando pagamento
    CONFIRMADO,  // Pagamento aprovado
    CANCELADO    // Pedido cancelado pelo sistema ou cliente
}
