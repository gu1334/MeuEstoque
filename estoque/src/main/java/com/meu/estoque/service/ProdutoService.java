package com.meu.estoque.service;


import com.meu.estoque.entities.Produto;
import com.meu.estoque.exseption.EstoqueInsuficienteException;
import com.meu.estoque.exseption.ProdutoNaoEncontradoException;
import com.meu.estoque.exseption.ValidacaoException;
import com.meu.estoque.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    /**
     * Retorna uma lista de produtos que estão vencidos ou próximos de vencer (nos próximos 7 dias).
     * Combina produtos cuja validade é hoje ou anterior com produtos cuja validade está entre amanhã e 7 dias.
     * @return Lista de produtos que precisam de atenção quanto à validade.
     */
    public List<Produto> verificarVencimentos() {
        LocalDate hoje = LocalDate.now();
        LocalDate proximaSemana = hoje.plusDays(7);

        // 1. Produtos que já venceram (data de validade menor ou igual a hoje)
        List<Produto> produtosVencidos = produtoRepository.findByValidadeLessThanEqual(hoje);

        // 2. Produtos que vencem nos próximos 7 dias (excluindo o dia de hoje, para evitar duplicatas se já estiverem em 'produtosVencidos')
        List<Produto> produtosProximosVencimento = produtoRepository.findByValidadeBetween(hoje.plusDays(1), proximaSemana);

        // Usa um Set para garantir que não haja produtos duplicados na lista final
        Set<Produto> todosAlertas = new HashSet<>(produtosVencidos);
        todosAlertas.addAll(produtosProximosVencimento);

        return new ArrayList<>(todosAlertas);
    }

    /**
     * Adiciona um novo produto ao estoque, com validações de negócio.
     * @param produto O objeto Produto a ser adicionado.
     * @return O produto salvo.
     * @throws ValidacaoException Se alguma regra de negócio for violada.
     */
    @Transactional
    public Produto adicionarProduto(Produto produto) {
        // Validações detalhadas para adicionar produto
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome do produto é obrigatório.");
        }
        if (produto.getLote() == null || produto.getLote().trim().isEmpty()) {
            throw new ValidacaoException("O lote do produto é obrigatório.");
        }
        if (produto.getQuantidade() < 0) {
            throw new ValidacaoException("A quantidade do produto não pode ser negativa. Valor fornecido: " + produto.getQuantidade() + ".");
        }
        if (produto.getQuantidadeMinima() < 0) {
            throw new ValidacaoException("A quantidade mínima não pode ser negativa. Valor fornecido: " + produto.getQuantidadeMinima() + ".");
        }
        if (produto.getQuantidadeMaxima() < 0) {
            throw new ValidacaoException("A quantidade máxima não pode ser negativa. Valor fornecido: " + produto.getQuantidadeMaxima() + ".");
        }
        if (produto.getQuantidadeMinima() > produto.getQuantidadeMaxima()) {
            throw new ValidacaoException("A quantidade mínima (" + produto.getQuantidadeMinima() + ") não pode ser maior que a quantidade máxima (" + produto.getQuantidadeMaxima() + ").");
        }
        if (produto.getValidade() == null) {
            throw new ValidacaoException("A data de validade é obrigatória.");
        }
        // Verifica se a validade não é no passado (permitindo a data de hoje para produtos que vencem hoje)
        if (produto.getValidade().isBefore(LocalDate.now())) {
            throw new ValidacaoException("A data de validade (" + produto.getValidade() + ") não pode ser no passado.");
        }

        // Você pode adicionar uma validação para verificar se já existe um produto com o mesmo nome e lote
        // Optional<Produto> existingProduct = produtoRepository.findByNomeAndLote(produto.getNome(), produto.getLote());
        // if (existingProduct.isPresent()) {
        //    throw new ValidacaoException("Já existe um produto com este nome e lote no estoque.");
        // }

        return produtoRepository.save(produto);
    }

    /**
     * Registra a saída de uma determinada quantidade de um produto específico.
     * @param nomeProduto O nome do produto.
     * @param quantidadeSaida A quantidade a ser retirada.
     * @param lote O lote do produto.
     * @return O produto com a quantidade atualizada.
     * @throws ProdutoNaoEncontradoException Se o produto não for encontrado.
     * @throws ValidacaoException Se a quantidade de saída for inválida.
     * @throws EstoqueInsuficienteException Se a quantidade em estoque for insuficiente.
     */
    @Transactional
    public Produto registrarSaida(String nomeProduto, int quantidadeSaida, String lote) {
        // Busca o produto pelo nome e lote
        Optional<Produto> produtoOpt = produtoRepository.findByNomeAndLote(nomeProduto, lote);

        if (produtoOpt.isEmpty()) {
            throw new ProdutoNaoEncontradoException("Produto com o nome '" + nomeProduto + "' e lote '" + lote + "' não encontrado para registrar a saída.");
        }

        Produto produto = produtoOpt.get();

        // Validações detalhadas para a saída
        if (quantidadeSaida <= 0) {
            throw new ValidacaoException("A quantidade de saída deve ser um número positivo. Valor fornecido: " + quantidadeSaida + ".");
        }
        if (produto.getQuantidade() < quantidadeSaida) {
            throw new EstoqueInsuficienteException("Quantidade insuficiente em estoque para o produto '" + produto.getNome() + "' (Lote: " + produto.getLote() + "). Disponível: " + produto.getQuantidade() + ", Solicitado: " + quantidadeSaida + ".");
        }

        produto.setQuantidade(produto.getQuantidade() - quantidadeSaida);
        return produtoRepository.save(produto);
    }

    /**
     * Lista produtos cuja quantidade atual é menor ou igual a um limite fixo (10, neste exemplo).
     * Idealmente, isso usaria o campo 'quantidadeMinima' da própria entidade.
     * @return Lista de produtos com quantidade abaixo do limite.
     */
    public List<Produto> listarProdutosAbaixoDoMinimo() {
        // Esta implementação busca produtos com quantidade <= 10.
        // Se você quiser que o limite mínimo seja o `quantidadeMinima` do próprio produto,
        // você precisaria de uma @Query ou Specification no repositório.
        // Exemplo de @Query: @Query("SELECT p FROM Produto p WHERE p.quantidade < p.quantidadeMinima")
        // No momento, apenas exemplifica o conceito de "abaixo do mínimo" com um valor fixo.
        return produtoRepository.findByQuantidadeLessThanEqual(10);
    }
}