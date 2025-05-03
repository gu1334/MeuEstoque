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
}
