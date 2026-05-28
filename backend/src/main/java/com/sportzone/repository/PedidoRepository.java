package com.sportzone.repository;

import com.sportzone.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// Repository para operações de persistência de Pedido
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
}
