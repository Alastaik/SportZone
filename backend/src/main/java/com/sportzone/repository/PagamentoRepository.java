package com.sportzone.repository;

import com.sportzone.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// Repository para operações de persistência de Pagamento
@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {
}
