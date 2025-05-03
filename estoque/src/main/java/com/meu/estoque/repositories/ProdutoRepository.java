package com.meu.estoque.repositories;

import com.meu.estoque.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    List<Produto> findByValidadeBefore(LocalDate data);
    List<Produto> findByQuantidadeLessThanEqual(int quantidadeMinima);

}
