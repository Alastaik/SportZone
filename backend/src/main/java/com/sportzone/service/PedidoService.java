package com.sportzone.service;

import com.sportzone.dto.PedidoDTO;
import com.sportzone.dto.PedidoResponseDTO;
import com.sportzone.model.ItemPedido;
import com.sportzone.model.Pagamento;
import com.sportzone.model.Pedido;
import com.sportzone.model.Produto;
import com.sportzone.model.enums.StatusPedido;
import com.sportzone.repository.PedidoRepository;
import com.sportzone.repository.ProdutoRepository;
import com.sportzone.strategy.PagamentoStrategy;
import com.sportzone.strategy.PagamentoStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Serviço responsável pelo fluxo de checkout de pedidos
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final PagamentoStrategyFactory pagamentoStrategyFactory;

    // Inicia o checkout: cria pedido, calcula total, processa pagamento e persiste
    @Transactional
    public PedidoResponseDTO iniciarCheckout(PedidoDTO dto) {
        log.info("Iniciando checkout com {} item(ns), método: {}", dto.itens().size(), dto.metodoPagamento());

        // 1. Criar pedido com status PENDENTE
        Pedido pedido = Pedido.builder()
                .dataPedido(LocalDateTime.now())
                .status(StatusPedido.PENDENTE)
                .build();

        // 2. Buscar cada produto e montar os itens com preço travado
        for (var itemDTO : dto.itens()) {
            Produto produto = produtoRepository.findById(itemDTO.produtoId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto não encontrado: " + itemDTO.produtoId()
                    ));

            ItemPedido item = ItemPedido.builder()
                    .produto(produto)
                    .quantidade(itemDTO.quantidade())
                    .precoUnitario(produto.getPreco())  // Trava o preço no momento da compra
                    .build();

            pedido.adicionarItem(item);
        }

        // 3. Calcular o valor total somando os subtotais
        pedido.calcularTotal();
        log.info("Valor total calculado: R$ {}", pedido.getValorTotal());

        // 4. Resolver a strategy de pagamento e processar
        PagamentoStrategy strategy = pagamentoStrategyFactory.getStrategy(dto.metodoPagamento());
        Pagamento pagamento = strategy.processar(pedido.getValorTotal());
        pedido.definirPagamento(pagamento);

        // 5. Confirmar pedido após pagamento aprovado
        pedido.confirmar();

        // 6. Persistir tudo (cascade salva itens + pagamento automaticamente)
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        log.info("Pedido {} salvo com sucesso. Status: {}", pedidoSalvo.getId(), pedidoSalvo.getStatus());

        // 7. Converter entidade para DTO de resposta
        return PedidoResponseDTO.fromEntity(pedidoSalvo);
    }
}
