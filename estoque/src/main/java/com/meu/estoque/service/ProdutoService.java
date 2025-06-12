package com.meu.estoque.service;

import com.meu.estoque.entities.Produto;
import com.meu.estoque.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList; // Importe ArrayList
import java.util.HashSet;   // Importe HashSet
import java.util.Set;       // Importe Set
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    // Método ajustado para verificar produtos vencidos E próximos do vencimento
    public List<Produto> verificarVencimentos() {
        LocalDate hoje = LocalDate.now();
        LocalDate proximaSemana = hoje.plusDays(7);

        // Busca produtos que já venceram (validade até hoje)
        List<Produto> produtosVencidos = produtoRepository.findByValidadeLessThanEqual(hoje);

        // Busca produtos que vencem nos próximos 7 dias (excluindo os que já venceram, se necessário)
        // Para evitar duplicatas e garantir que vencidos não sejam contados novamente,
        // usaremos um Set para coletar todos os produtos e depois converter para List.
        List<Produto> produtosProximosVencimento = produtoRepository.findByValidadeBetween(hoje.plusDays(1), proximaSemana); // Começa a partir de amanhã

        Set<Produto> todosAlertas = new HashSet<>(produtosVencidos);
        todosAlertas.addAll(produtosProximosVencimento); // Adiciona sem duplicar

        return new ArrayList<>(todosAlertas);
    }

    @Transactional
    public Produto adicionarProduto(Produto produto) {
        if (produto.getQuantidade() < 0) {
            throw new IllegalArgumentException("A quantidade do produto não pode ser negativa.");
        }
        if (produto.getQuantidadeMinima() < 0) {
            throw new IllegalArgumentException("A quantidade mínima não pode ser negativa.");
        }
        if (produto.getQuantidadeMaxima() < 0) {
            throw new IllegalArgumentException("A quantidade máxima não pode ser negativa.");
        }
        if (produto.getQuantidadeMinima() > produto.getQuantidadeMaxima()) {
            throw new IllegalArgumentException("A quantidade mínima não pode ser maior que a quantidade máxima.");
        }
        if (produto.getValidade() != null && produto.getValidade().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de validade não pode ser no passado.");
        }
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto registrarSaida(String nomeProduto, int quantidadeSaida, String lote) {
        Optional<Produto> produtoOpt = produtoRepository.findByNomeAndLote(nomeProduto, lote);

        if (produtoOpt.isEmpty()) {
            throw new RuntimeException("Produto com o nome '" + nomeProduto + "' e lote '" + lote + "' não encontrado.");
        }

        Produto produto = produtoOpt.get();

        if (quantidadeSaida <= 0) {
            throw new IllegalArgumentException("A quantidade de saída deve ser um número positivo.");
        }
        if (produto.getQuantidade() < quantidadeSaida) {
            throw new IllegalArgumentException("Quantidade insuficiente no estoque. Disponível: " + produto.getQuantidade() + ".");
        }

        produto.setQuantidade(produto.getQuantidade() - quantidadeSaida);
        return produtoRepository.save(produto);
    }

    public List<Produto> listarProdutosAbaixoDoMinimo() {
        // Isso ainda busca produtos com quantidade <= 10.
        // Para usar a 'quantidadeMinima' do próprio produto, você precisaria de uma @Query
        // @Query("SELECT p FROM Produto p WHERE p.quantidade < p.quantidadeMinima")
        // List<Produto> findByQuantidadeLessThanMinima();
        return produtoRepository.findByQuantidadeLessThanEqual(10);
    }
}