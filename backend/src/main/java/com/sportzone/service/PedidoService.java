package com.sportzone.service;

import com.sportzone.dto.PedidoDTO;
import com.sportzone.dto.PedidoResponseDTO;
import com.sportzone.event.PedidoCriadoEvent;
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

import java.time.LocalDateTime;
import java.util.UUID;

// Serviço responsável pela criação e processamento assíncrono de pedidos
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final PagamentoStrategyFactory pagamentoStrategyFactory;

    // Cria o pedido com status PROCESSANDO_PAGAMENTO e retorna o evento para o Kafka
    // Chamado pelo Controller — execução SÍNCRONA (rápida, não bloqueia)
    @Transactional
    public PedidoCriadoEvent criarPedido(PedidoDTO dto) {
        log.info("Criando pedido com {} item(ns), método: {}", dto.itens().size(), dto.metodoPagamento());

        // 1. Criar pedido com status inicial da máquina de estados
        Pedido pedido = Pedido.builder()
                .dataPedido(LocalDateTime.now())
                .status(StatusPedido.PROCESSANDO_PAGAMENTO)
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

        // 4. Persistir pedido (cascade salva itens automaticamente)
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        log.info("Pedido {} salvo com status: {}", pedidoSalvo.getId(), pedidoSalvo.getStatus());

        // 5. Retornar evento para o Producer publicar no Kafka
        return new PedidoCriadoEvent(
                pedidoSalvo.getId(),
                pedidoSalvo.getValorTotal(),
                dto.metodoPagamento()
        );
    }

    // Processa o pagamento via Strategy e avança o status
    // Chamado pelo Worker — execução ASSÍNCRONA (background)
    @Transactional
    public void processarPagamento(PedidoCriadoEvent evento) {
        Pedido pedido = buscarPedido(evento.pedidoId());

        // Resolver e executar a strategy de pagamento
        PagamentoStrategy strategy = pagamentoStrategyFactory.getStrategy(evento.metodoPagamento());
        Pagamento pagamento = strategy.processar(evento.valorTotal());
        pedido.definirPagamento(pagamento);

        // Avançar: PROCESSANDO_PAGAMENTO → SEPARANDO_ESTOQUE
        pedido.avancarStatus();
        pedidoRepository.save(pedido);

        log.info("Pedido {} — Pagamento processado. Novo status: {}", pedido.getId(), pedido.getStatus());
    }

    // Avança o pedido para o próximo estado da máquina de estados
    // Chamado pelo Worker — execução ASSÍNCRONA (background)
    @Transactional
    public void avancarStatus(UUID pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
        StatusPedido statusAnterior = pedido.getStatus();

        pedido.avancarStatus();
        pedidoRepository.save(pedido);

        log.info("Pedido {} — Status: {} → {}", pedidoId, statusAnterior, pedido.getStatus());
    }

    // Busca o pedido por ID e retorna DTO de resposta (para consulta)
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(UUID pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
        return PedidoResponseDTO.fromEntity(pedido);
    }

    // Busca o pedido ou lança exceção se não encontrado
    private Pedido buscarPedido(UUID pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido não encontrado: " + pedidoId
                ));
    }
}
