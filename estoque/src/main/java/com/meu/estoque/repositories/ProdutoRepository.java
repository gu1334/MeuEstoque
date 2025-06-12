package com.meu.estoque.repositories;

import com.meu.estoque.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    // Busca produtos que já venceram (validade anterior ou igual à data fornecida)
    List<Produto> findByValidadeLessThanEqual(LocalDate data);

    // Busca produtos cuja validade está entre duas datas (ex: amanhã e daqui a 7 dias)
    List<Produto> findByValidadeBetween(LocalDate dataInicio, LocalDate dataFim);

    // Método para encontrar um produto específico pelo nome e lote, crucial para a saída
    Optional<Produto> findByNomeAndLote(String nome, String lote);

    // Mantido como estava. Pode ser melhorado com @Query para usar o campo quantidadeMinima do próprio Produto.
    List<Produto> findByQuantidadeLessThanEqual(int quantidadeLimite);

}