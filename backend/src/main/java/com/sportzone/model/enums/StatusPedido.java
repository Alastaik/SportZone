package com.sportzone.model.enums;

// Máquina de estados do pedido — cada status conhece seu próximo estado válido
public enum StatusPedido {

    PROCESSANDO_PAGAMENTO,  // Pedido criado, pagamento sendo processado
    SEPARANDO_ESTOQUE,      // Pagamento aprovado, itens sendo separados
    ENVIADO,                // Pedido despachado para entrega
    ENTREGUE,               // Pedido entregue ao cliente
    CANCELADO;              // Pedido cancelado (estado terminal)

    // Retorna o próximo status na sequência do fluxo
    // Fluxo: PROCESSANDO_PAGAMENTO → SEPARANDO_ESTOQUE → ENVIADO → ENTREGUE
    public StatusPedido proximo() {
        return switch (this) {
            case PROCESSANDO_PAGAMENTO -> SEPARANDO_ESTOQUE;
            case SEPARANDO_ESTOQUE -> ENVIADO;
            case ENVIADO -> ENTREGUE;
            case ENTREGUE, CANCELADO ->
                    throw new IllegalStateException("O pedido no status " + this + " não pode avançar.");
        };
    }

    // Verifica se o pedido ainda pode transitar para o próximo estado
    public boolean podeAvancar() {
        return this != ENTREGUE && this != CANCELADO;
    }
}
