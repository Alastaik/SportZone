package com.sportzone.repository;

import com.sportzone.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// Repository para operações de persistência de Produto
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
}
