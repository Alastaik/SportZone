package com.sportzone.model.enums;

// Representa o resultado do processamento de um pagamento
public enum StatusPagamento {
    PENDENTE,   // Aguardando processamento
    APROVADO,   // Pagamento confirmado com sucesso
    RECUSADO    // Pagamento negado ou estornado
}
