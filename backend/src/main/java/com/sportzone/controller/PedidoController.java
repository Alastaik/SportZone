package com.sportzone.controller;

import com.sportzone.dto.PedidoDTO;
import com.sportzone.dto.PedidoResponseDTO;
import com.sportzone.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller REST para o fluxo de checkout de pedidos
@Slf4j
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // POST /api/pedidos — Recebe os dados do checkout e retorna o pedido processado
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> iniciarCheckout(@RequestBody @Valid PedidoDTO dto) {
        log.info("POST /api/pedidos — Checkout recebido");
        PedidoResponseDTO response = pedidoService.iniciarCheckout(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
