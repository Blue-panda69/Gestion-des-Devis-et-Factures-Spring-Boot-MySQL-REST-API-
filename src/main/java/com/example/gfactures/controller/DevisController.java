package com.example.gfactures.controller;

import com.example.gfactures.model.Devis;
import com.example.gfactures.model.DevisDetail;
import com.example.gfactures.model.Produit;
import com.example.gfactures.repository.DevisRepository;
import com.example.gfactures.repository.ProduitRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/devis")
public class DevisController {

    private final DevisRepository repository;
    private final ProduitRepository produitRepository;

    public DevisController(DevisRepository repository, ProduitRepository produitRepository) {
        this.repository = repository;
        this.produitRepository = produitRepository;
    }

    @GetMapping
    public List<Devis> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Devis getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public Devis create(@RequestBody Devis devis) {
        prepareDevis(devis);
        return repository.save(devis);
    }

    @PutMapping("/{id}")
    public Devis update(@PathVariable Long id, @RequestBody Devis devis) {
        devis.setId(id);
        prepareDevis(devis);
        return repository.save(devis);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

    /**
     * Attach the devis to each detail, hydrate missing unit prices from the product,
     * and compute totals (HT + TTC).
     */
    private void prepareDevis(Devis devis) {
        BigDecimal totalHt = BigDecimal.ZERO;

        if (devis.getDetails() != null) {
            for (DevisDetail detail : devis.getDetails()) {
                detail.setDevis(devis);
                // If prixUnitaire not provided, load from product
                if (detail.getPrixUnitaire() == null && detail.getProduit() != null && detail.getProduit().getId() != null) {
                    produitRepository.findById(detail.getProduit().getId())
                            .map(Produit::getPrixUnitaire)
                            .ifPresent(detail::setPrixUnitaire);
                }

                BigDecimal pu = detail.getPrixUnitaire() != null ? detail.getPrixUnitaire() : BigDecimal.ZERO;
                int qty = detail.getQuantite() != null ? detail.getQuantite() : 0;
                totalHt = totalHt.add(pu.multiply(BigDecimal.valueOf(qty)));
            }
        }

        devis.setTotalHt(totalHt);
        // For now, TTC = HT (no VAT handling). Adjust here if you add VAT.
        devis.setTotalTtc(totalHt);
    }
}


