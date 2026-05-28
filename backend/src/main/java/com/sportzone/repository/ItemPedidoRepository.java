package com.sportzone.repository;

import com.sportzone.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// Repository para operações de persistência de ItemPedido
@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, UUID> {
}
