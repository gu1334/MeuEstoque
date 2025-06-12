package com.meu.estoque.repositories;

import com.meu.estoque.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    // Busca produtos que já venceram (validade antes ou igual à data fornecida)
    List<Produto> findByValidadeLessThanEqual(LocalDate data);

    // Busca produtos cuja validade está entre duas datas
    List<Produto> findByValidadeBetween(LocalDate dataInicio, LocalDate dataFim);

    // Mantém este método, que está correto
    List<Produto> findByQuantidadeLessThanEqual(int quantidadeLimite);

    // Mantém este método, que está correto
    Optional<Produto> findByNomeAndLote(String nome, String lote);
}