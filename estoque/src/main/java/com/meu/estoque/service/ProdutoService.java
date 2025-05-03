package com.meu.estoque.service;

import com.meu.estoque.entities.Produto;
import com.meu.estoque.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Adiciona um novo produto
    public Produto adicionarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    // Obtém todos os produtos
    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    // Verifica produtos com validade próxima
    public List<Produto> verificarVencimentos() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7); // Alerta 7 dias antes da data de vencimento
        return produtoRepository.findByValidadeBefore(limite);
    }
}

