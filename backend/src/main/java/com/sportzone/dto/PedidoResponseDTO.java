package com.sportzone.dto;

import com.sportzone.model.Pedido;
import com.sportzone.model.enums.MetodoPagamento;
import com.sportzone.model.enums.StatusPagamento;
import com.sportzone.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// DTO de resposta — dados completos do pedido após o checkout
public record PedidoResponseDTO(
        UUID pedidoId,
        LocalDateTime dataPedido,
        StatusPedido statusPedido,
        BigDecimal valorTotal,
        List<ItemResponseDTO> itens,
        PagamentoResponseDTO pagamento
) {

    // Dados de cada item no pedido processado
    public record ItemResponseDTO(
            UUID itemId,
            UUID produtoId,
            String produtoNome,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal
    ) {
    }

    // Dados do pagamento processado
    public record PagamentoResponseDTO(
            UUID pagamentoId,
            MetodoPagamento metodo,
            StatusPagamento status,
            BigDecimal valor,
            String transacaoId
    ) {
    }

    // Converte a entidade Pedido (persistida) em DTO de resposta
    public static PedidoResponseDTO fromEntity(Pedido pedido) {

        // Mapeia cada ItemPedido para ItemResponseDTO
        List<ItemResponseDTO> itensDTO = pedido.getItens().stream()
                .map(item -> new ItemResponseDTO(
                        item.getId(),
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.calcularSubtotal()
                ))
                .toList();

        // Mapeia o Pagamento para PagamentoResponseDTO
        PagamentoResponseDTO pagamentoDTO = null;
        if (pedido.getPagamento() != null) {
            var pag = pedido.getPagamento();
            pagamentoDTO = new PagamentoResponseDTO(
                    pag.getId(),
                    pag.getMetodo(),
                    pag.getStatus(),
                    pag.getValor(),
                    pag.getTransacaoId()
            );
        }

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getDataPedido(),
                pedido.getStatus(),
                pedido.getValorTotal(),
                itensDTO,
                pagamentoDTO
        );
    }
}
