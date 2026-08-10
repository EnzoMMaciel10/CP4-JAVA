package br.com.fiap.mercado_express.controller;

import br.com.fiap.mercado_express.model.Mercado;
import br.com.fiap.mercado_express.repository.MercadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mercado")
public class MercadoController {

    @Autowired
    private MercadoRepository repository;

    // Lista em memória exigida pelo documento para POST, PUT e PATCH
    private List<Mercado> listaMemoria = new ArrayList<>();

    // READ - Listar Todos
    @GetMapping
    public ResponseEntity<List<Mercado>> listarTodos() {
        List<Mercado> mercados = repository.findAll();
        for (Mercado mercado : mercados) {
            Long id = mercado.getId();
            // Implementação do HATEOAS
            mercado.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoController.class).consultarPorId(id)).withSelfRel());
        }
        return ResponseEntity.ok(mercados);
    }

    // READ - Consultar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Mercado> consultarPorId(@PathVariable Long id) {
        Optional<Mercado> mercadoO = repository.findById(id);
        if (mercadoO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Mercado mercado = mercadoO.get();
        // Implementação do HATEOAS
        mercado.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoController.class).listarTodos()).withRel("Lista de Produtos"));
        return ResponseEntity.ok(mercado);
    }

    // CREATE - POST
    @PostMapping
    public ResponseEntity<Mercado> criar(@RequestBody Mercado mercado) {
        listaMemoria.add(mercado); // Envia para a lista do programa
        Mercado salvo = repository.save(mercado); // Commit no Oracle
        salvo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoController.class).consultarPorId(salvo.getId())).withSelfRel());
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // UPDATE COMPLETO - PUT
    @PutMapping("/{id}")
    public ResponseEntity<Mercado> atualizar(@PathVariable Long id, @RequestBody Mercado mercadoAtualizado) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        mercadoAtualizado.setId(id);
        listaMemoria.add(mercadoAtualizado); // Envia para a lista do programa
        Mercado salvo = repository.save(mercadoAtualizado); // Commit no Oracle
        salvo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoController.class).consultarPorId(salvo.getId())).withSelfRel());
        return ResponseEntity.ok(salvo);
    }

    // UPDATE PARCIAL - PATCH
    @PatchMapping("/{id}")
    public ResponseEntity<Mercado> atualizarParcial(@PathVariable Long id, @RequestBody Mercado mercadoAtualizado) {
        Optional<Mercado> mercadoO = repository.findById(id);
        if (mercadoO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Mercado mercadoExistente = mercadoO.get();

        // Atualiza apenas os campos que vierem preenchidos no JSON
        if (mercadoAtualizado.getNome() != null) mercadoExistente.setNome(mercadoAtualizado.getNome());
        if (mercadoAtualizado.getTipo() != null) mercadoExistente.setTipo(mercadoAtualizado.getTipo());
        if (mercadoAtualizado.getSetor() != null) mercadoExistente.setSetor(mercadoAtualizado.getSetor());
        if (mercadoAtualizado.getTamanho() != null) mercadoExistente.setTamanho(mercadoAtualizado.getTamanho());
        if (mercadoAtualizado.getPreco() != null) mercadoExistente.setPreco(mercadoAtualizado.getPreco());

        listaMemoria.add(mercadoExistente); // Envia para a lista do programa
        Mercado salvo = repository.save(mercadoExistente); // Commit no Oracle
        salvo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoController.class).consultarPorId(salvo.getId())).withSelfRel());
        return ResponseEntity.ok(salvo);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id); // Exclusão pelo ID no Banco
        return ResponseEntity.noContent().build();
    }
}