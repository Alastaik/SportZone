package com.sportzone.controller;

import com.sportzone.dto.PedidoDTO;
import com.sportzone.dto.PedidoResponseDTO;
import com.sportzone.event.PedidoCriadoEvent;
import com.sportzone.kafka.PedidoProducer;
import com.sportzone.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoProducer pedidoProducer;

    @PostMapping
    public ResponseEntity<Map<String, Object>> iniciarCheckout(@RequestBody @Valid PedidoDTO dto) {
        log.info("POST /api/pedidos — Checkout recebido");

        // Salvar pedido inicial (Síncrono)
        PedidoCriadoEvent evento = pedidoService.criarPedido(dto);

        // Publicar evento para processamento em background (Assíncrono)
        pedidoProducer.enviar(evento);

        log.info("Pedido {} aceito para processamento", evento.pedidoId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "mensagem", "Pedido recebido e em processamento",
                "pedidoId", evento.pedidoId(),
                "status", "PROCESSANDO_PAGAMENTO"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> consultarPedido(@PathVariable UUID id) {
        log.info("GET /api/pedidos/{} — Consulta de status", id);
        PedidoResponseDTO response = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
}
