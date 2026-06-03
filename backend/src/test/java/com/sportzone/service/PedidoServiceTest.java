package com.sportzone.service;

import com.sportzone.dto.ItemPedidoDTO;
import com.sportzone.dto.PedidoDTO;
import com.sportzone.dto.PedidoResponseDTO;
import com.sportzone.event.PedidoCriadoEvent;
import com.sportzone.model.Pagamento;
import com.sportzone.model.Pedido;
import com.sportzone.model.Produto;
import com.sportzone.model.enums.MetodoPagamento;
import com.sportzone.model.enums.StatusPagamento;
import com.sportzone.model.enums.StatusPedido;
import com.sportzone.repository.PedidoRepository;
import com.sportzone.repository.ProdutoRepository;
import com.sportzone.strategy.PagamentoStrategy;
import com.sportzone.strategy.PagamentoStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PagamentoStrategyFactory pagamentoStrategyFactory;

    @Mock
    private PagamentoStrategy pagamentoStrategy;

    @InjectMocks
    private PedidoService pedidoService;

    private UUID produtoId;
    private UUID pedidoId;
    private Produto produtoMock;
    private Pedido pedidoMock;

    @BeforeEach
    void setUp() {
        produtoId = UUID.randomUUID();
        pedidoId = UUID.randomUUID();

        produtoMock = Produto.builder()
                .id(produtoId)
                .nome("Tênis de Corrida")
                .preco(new BigDecimal("200.00"))
                .build();

        pedidoMock = Pedido.builder()
                .id(pedidoId)
                .status(StatusPedido.PROCESSANDO_PAGAMENTO)
                .build();
    }

    @Test
    void criarPedido_ComSucesso() {
        ItemPedidoDTO itemDto = new ItemPedidoDTO(produtoId, 2);
        PedidoDTO dto = new PedidoDTO(List.of(itemDto), MetodoPagamento.PIX);

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoMock));
        
        Pedido pedidoSalvo = Pedido.builder()
                .id(pedidoId)
                .status(StatusPedido.PROCESSANDO_PAGAMENTO)
                .build();
        pedidoSalvo.adicionarItem(com.sportzone.model.ItemPedido.builder()
                .produto(produtoMock)
                .quantidade(2)
                .precoUnitario(new BigDecimal("200.00"))
                .build());
        pedidoSalvo.calcularTotal();

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvo);

        PedidoCriadoEvent evento = pedidoService.criarPedido(dto);

        assertNotNull(evento);
        assertEquals(pedidoId, evento.pedidoId());
        assertEquals(MetodoPagamento.PIX, evento.metodoPagamento());
        assertEquals(new BigDecimal("400.00"), evento.valorTotal());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void criarPedido_ProdutoNaoEncontrado_DeveLancarExcecao() {
        ItemPedidoDTO itemDto = new ItemPedidoDTO(produtoId, 1);
        PedidoDTO dto = new PedidoDTO(List.of(itemDto), MetodoPagamento.CARTAO_CREDITO);

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> pedidoService.criarPedido(dto));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void processarPagamento_ComSucesso() {
        PedidoCriadoEvent evento = new PedidoCriadoEvent(pedidoId, new BigDecimal("200.00"), MetodoPagamento.CARTAO_CREDITO);
        
        Pagamento pagamentoMock = Pagamento.builder()
                .status(StatusPagamento.APROVADO)
                .metodo(MetodoPagamento.CARTAO_CREDITO)
                .valor(new BigDecimal("200.00"))
                .transacaoId("CARD-123")
                .build();

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoMock));
        when(pagamentoStrategyFactory.getStrategy(MetodoPagamento.CARTAO_CREDITO)).thenReturn(pagamentoStrategy);
        when(pagamentoStrategy.processar(new BigDecimal("200.00"))).thenReturn(pagamentoMock);

        pedidoService.processarPagamento(evento);

        assertEquals(StatusPedido.SEPARANDO_ESTOQUE, pedidoMock.getStatus());
        assertNotNull(pedidoMock.getPagamento());
        assertEquals(StatusPagamento.APROVADO, pedidoMock.getPagamento().getStatus());
        verify(pedidoRepository).save(pedidoMock);
    }

    @Test
    void avancarStatus_ComSucesso() {
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoMock)); // Inicialmente PROCESSANDO_PAGAMENTO

        pedidoService.avancarStatus(pedidoId);

        assertEquals(StatusPedido.SEPARANDO_ESTOQUE, pedidoMock.getStatus());
        verify(pedidoRepository).save(pedidoMock);
    }

    @Test
    void buscarPorId_Existente_DeveRetornarDTO() {
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoMock));

        PedidoResponseDTO response = pedidoService.buscarPorId(pedidoId);

        assertNotNull(response);
        assertEquals(pedidoId, response.pedidoId());
        assertEquals(StatusPedido.PROCESSANDO_PAGAMENTO, response.statusPedido());
    }

    @Test
    void buscarPedido_NaoEncontrado_DeveLancarExcecao() {
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> pedidoService.buscarPorId(pedidoId));
    }
}
