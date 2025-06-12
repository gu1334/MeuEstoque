package com.meu.estoque.controller;

import com.meu.estoque.dto.ProdutoDTO;
import com.meu.estoque.entities.Produto;
import com.meu.estoque.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Importar para o mapa de erro

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping("/adicionar")
    public ResponseEntity<Produto> adicionarProduto(@RequestBody Produto produto) {
        Produto novoProduto = produtoService.adicionarProduto(produto);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }

    @PostMapping("/saida")
    public ResponseEntity<?> registrarSaida(@RequestBody Map<String, Object> payload) {
        try {
            String nomeProduto = (String) payload.get("nomeProduto");
            // Cuidado ao obter Integer de Map: pode ser Double se o JSON tiver casas decimais
            Integer quantidadeSaida = ((Number) payload.get("quantidadeSaida")).intValue();
            String lote = (String) payload.get("lote");

            // Validações básicas de entrada do Controller
            if (nomeProduto == null || nomeProduto.trim().isEmpty() || quantidadeSaida == null || quantidadeSaida <= 0 || lote == null || lote.trim().isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Nome do produto, quantidade de saída e lote são obrigatórios e a quantidade deve ser positiva."), HttpStatus.BAD_REQUEST);
            }

            Produto produtoAtualizado = produtoService.registrarSaida(nomeProduto, quantidadeSaida, lote);
            return new ResponseEntity<>(produtoAtualizado, HttpStatus.OK);
        } catch (ClassCastException e) {
            return new ResponseEntity<>(Map.of("message", "A quantidade de saída deve ser um número inteiro válido."), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) { // Captura exceções de validação do service
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) { // Captura RuntimeException para produto não encontrado
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) { // Captura qualquer outra exceção inesperada
            return new ResponseEntity<>(Map.of("message", "Erro interno ao registrar saída: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/listar")
    public List<Produto> listarProdutos() {
        return produtoService.listarProdutos();
    }

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

    @GetMapping("/compras")
    public List<ProdutoDTO> listaDeCompras() {
        List<Produto> produtos = produtoService.listarProdutosAbaixoDoMinimo();
        List<ProdutoDTO> produtosDTO = new ArrayList<>();

        for (Produto produto : produtos) {
            produtosDTO.add(new ProdutoDTO(produto.getNome(), produto.getQuantidade(), 0));
        }

        return produtosDTO;
    }

    // --- Tratamento de Erros Global para o Controller ---
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        // Pode ser mais específico aqui se você tiver outras RuntimeExceptions
        return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        return new ResponseEntity<>(Map.of("message", "Ocorreu um erro interno no servidor."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}