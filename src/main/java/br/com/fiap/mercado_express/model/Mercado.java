package br.com.fiap.mercado_express.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TDS_TB_mercado")
public class Mercado extends RepresentationModel<Mercado> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String tipo;
    private String setor;
    private String tamanho;
    private Double preco;
}