package com.meu.estoque.controller;

import com.meu.estoque.dto.ProdutoDTO;
import com.meu.estoque.entities.Produto;

import com.meu.estoque.exseption.EstoqueInsuficienteException;
import com.meu.estoque.exseption.ProdutoNaoEncontradoException;
import com.meu.estoque.exseption.ValidacaoException;
import com.meu.estoque.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    /**
     * Endpoint para adicionar um novo produto ao estoque.
     * @param produto Objeto Produto com os dados a serem salvos.
     * @return ResponseEntity com o produto salvo e status 201 Created.
     */
    @PostMapping("/adicionar")
    public ResponseEntity<Produto> adicionarProduto(@RequestBody Produto produto) {
        // A validação detalhada é feita no serviço.
        // Se houver ValidacaoException, será capturada pelo handler global.
        Produto novoProduto = produtoService.adicionarProduto(produto);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }

    /**
     * Endpoint para registrar a saída de um produto do estoque.
     * @param payload Um mapa contendo nomeProduto, quantidadeSaida e lote.
     * @return ResponseEntity com o produto atualizado e status 200 OK.
     */
    @PostMapping("/saida")
    public ResponseEntity<?> registrarSaida(@RequestBody Map<String, Object> payload) {
        try {
            String nomeProduto = (String) payload.get("nomeProduto");
            // Converte para Integer de forma segura para evitar ClassCastException se for Double no JSON.
            Integer quantidadeSaida = payload.get("quantidadeSaida") instanceof Number ?
                    ((Number) payload.get("quantidadeSaida")).intValue() : null;
            String lote = (String) payload.get("lote");

            // Validação de entrada básica para tipos nulos/vazios antes de chamar o serviço.
            if (nomeProduto == null || nomeProduto.trim().isEmpty() || quantidadeSaida == null || lote == null || lote.trim().isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Nome do produto, quantidade de saída e lote são campos obrigatórios."), HttpStatus.BAD_REQUEST);
            }

            Produto produtoAtualizado = produtoService.registrarSaida(nomeProduto, quantidadeSaida, lote);
            return new ResponseEntity<>(produtoAtualizado, HttpStatus.OK);
        } catch (ClassCastException e) {
            // Se a quantidadeSaida não for um número que possa ser convertido.
            return new ResponseEntity<>(Map.of("message", "A quantidade de saída deve ser um número válido."), HttpStatus.BAD_REQUEST);
        }
        // As exceções customizadas (ProdutoNaoEncontradoException, EstoqueInsuficienteException, ValidacaoException)
        // serão tratadas pelos @ExceptionHandler abaixo, que fornecem mensagens mais específicas.
    }

    /**
     * Endpoint para listar todos os produtos no estoque.
     * @return Uma lista de objetos Produto.
     */
    @GetMapping("/listar")
    public List<Produto> listarProdutos() {
        return produtoService.listarProdutos();
    }

    /**
     * Endpoint para verificar produtos com validade próxima ou vencida.
     * @return Uma lista de ProdutoDTO com nome, quantidade e dias restantes para o vencimento.
     */
    @GetMapping("/vencimentos")
    public List<ProdutoDTO> verificarVencimentos() {
        List<Produto> produtos = produtoService.verificarVencimentos();
        List<ProdutoDTO> produtosDTO = new ArrayList<>();

        for (Produto produto : produtos) {
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), produto.getValidade());
            produtosDTO.add(new ProdutoDTO(produto.getNome(), produto.getQuantidade(), diasRestantes));
        }

        return produtosDTO;
    }

    /**
     * Endpoint para listar produtos que estão abaixo da quantidade mínima.
     * @return Uma lista de ProdutoDTO com nome e quantidade.
     */
    @GetMapping("/compras")
    public List<ProdutoDTO> listaDeCompras() {
        List<Produto> produtos = produtoService.listarProdutosAbaixoDoMinimo();
        List<ProdutoDTO> produtosDTO = new ArrayList<>();

        for (Produto produto : produtos) {
            // Dias restantes é 0 aqui porque não é o foco desta lista.
            produtosDTO.add(new ProdutoDTO(produto.getNome(), produto.getQuantidade(), 0));
        }

        return produtosDTO;
    }

    // --- Tratamento de Erros Global para o Controller (usando exceções customizadas) ---

    /**
     * Captura ValidacaoException e retorna um status HTTP 400 Bad Request.
     * As mensagens são as definidas nas exceções do serviço.
     * @param e A exceção ValidacaoException lançada.
     * @return ResponseEntity com a mensagem de erro e status 400.
     */
    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Map<String, String>> handleValidacaoException(ValidacaoException e) {
        return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Captura ProdutoNaoEncontradoException e retorna um status HTTP 404 Not Found.
     * @param e A exceção ProdutoNaoEncontradoException lançada.
     * @return ResponseEntity com a mensagem de erro e status 404.
     */
    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleProdutoNaoEncontradoException(ProdutoNaoEncontradoException e) {
        return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Captura EstoqueInsuficienteException e retorna um status HTTP 400 Bad Request.
     * @param e A exceção EstoqueInsuficienteException lançada.
     * @return ResponseEntity com a mensagem de erro e status 400.
     */
    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<Map<String, String>> handleEstoqueInsuficienteException(EstoqueInsuficienteException e) {
        return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Captura qualquer outra exceção genérica e retorna um status HTTP 500 Internal Server Error.
     * É um fallback para erros inesperados.
     * @param e A exceção genérica lançada.
     * @return ResponseEntity com uma mensagem de erro genérica e status 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        System.err.println("Erro interno inesperado no servidor: " + e.getMessage()); // Log para o desenvolvedor
        return new ResponseEntity<>(Map.of("message", "Ocorreu um erro inesperado no servidor. Por favor, tente novamente mais tarde."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}