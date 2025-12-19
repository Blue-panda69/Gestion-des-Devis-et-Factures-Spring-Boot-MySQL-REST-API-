package com.example.gfactures.controller;

import com.example.gfactures.model.Facture;
import com.example.gfactures.repository.FactureRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/factures")
public class FactureController {

    private final FactureRepository repository;

    public FactureController(FactureRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Facture> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Facture getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public Facture create(@RequestBody Facture facture) {
        return repository.save(facture);
    }

    @PutMapping("/{id}")
    public Facture update(@PathVariable Long id, @RequestBody Facture facture) {
        facture.setId(id);
        return repository.save(facture);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}


