package br.com.alterdata.vendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categorias")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categorias_seq")
    @SequenceGenerator(name = "categorias_seq", sequenceName = "categorias_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private String nome;

}
