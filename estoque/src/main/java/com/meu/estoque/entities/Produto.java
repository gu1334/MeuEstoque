package com.meu.estoque.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produto_id")
    private Integer id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "validade", nullable = false)
    private LocalDate validade;

    @Column(name = "lote", nullable = false)
    private String lote;

    @Column(name = "quantidade_minima", nullable = false)
    private int quantidadeMinima;

    @Column(name = "quantidade_maxima", nullable = false)
    private int quantidadeMaxima;

    public Produto(Integer id, String nome, int quantidade, LocalDate validade, String lote, int quantidadeMinima, int quantidadeMaxima) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.validade = validade;
        this.lote = lote;
        this.quantidadeMinima = quantidadeMinima;
        this.quantidadeMaxima = quantidadeMaxima;
    }
}