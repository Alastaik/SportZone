package com.sportzone.dto;

import com.sportzone.model.enums.MetodoPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// DTO de entrada — dados necessários para iniciar o checkout
public record PedidoDTO(

        @NotEmpty(message = "O pedido deve conter ao menos um item")
        @Valid  // Propaga validação para cada ItemPedidoDTO
        List<ItemPedidoDTO> itens,

        @NotNull(message = "O método de pagamento é obrigatório")
        MetodoPagamento metodoPagamento  // CARTAO_CREDITO ou PIX
) {
}
