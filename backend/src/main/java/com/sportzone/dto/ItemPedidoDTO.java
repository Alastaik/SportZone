package com.sportzone.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// DTO de entrada — representa um item no checkout
public record ItemPedidoDTO(

        @NotNull(message = "O ID do produto é obrigatório")
        UUID produtoId,  // ID do produto sendo comprado

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade mínima é 1")
        Integer quantidade  // Quantidade desejada
) {
}
