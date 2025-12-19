package com.example.gfactures.controller;

import com.example.gfactures.model.Facture;
import com.example.gfactures.repository.FactureRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/pdf")
public class FacturePdfController {

    private final FactureRepository factureRepository;

    public FacturePdfController(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    @GetMapping("/facture/{id}")
    public ResponseEntity<byte[]> exportFacture(@PathVariable Long id) throws Exception {
        Facture facture = factureRepository.findById(id).orElseThrow();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        document.add(new Paragraph("Facture #" + facture.getId()));
        if (facture.getClient() != null) {
            document.add(new Paragraph("Client: " + facture.getClient().getNom()));
        }
        document.add(new Paragraph("Date: " + facture.getDate()));
        document.add(new Paragraph("Montant TTC: " + facture.getMontantTtc()));
        document.add(new Paragraph("Mode paiement: " + facture.getModePaiement()));
        document.add(new Paragraph("Statut: " + facture.getStatut()));

        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture-" + facture.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(baos.toByteArray());
    }
}


