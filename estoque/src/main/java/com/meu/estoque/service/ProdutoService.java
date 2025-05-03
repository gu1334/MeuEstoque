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

    public Produto adicionarProduto(Produto produto) {
        if (produto.getQuantidade() < 0) {
            throw new IllegalArgumentException("A quantidade do produto não pode ser negativa.");
        }
        return produtoRepository.save(produto);
    }

    public Produto retirarProduto(Integer id, int quantidade) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getQuantidade() < quantidade) {
            throw new IllegalArgumentException("Quantidade insuficiente no estoque.");
        }

        produto.setQuantidade(produto.getQuantidade() - quantidade);
        return produtoRepository.save(produto);
    }

    public List<Produto> listarProdutosAbaixoDoMinimo() {
        // Busca os produtos abaixo da quantidade mínima
        return produtoRepository.findByQuantidadeLessThanEqual(10); // Usar o campo `quantidadeMinima` se desejar
    }
}
