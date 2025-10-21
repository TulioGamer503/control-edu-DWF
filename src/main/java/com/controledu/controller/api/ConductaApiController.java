package com.controledu.controller.api;

import com.controledu.model.Conducta;
import com.controledu.service.ConductaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conductas")
@RequiredArgsConstructor
@Tag(name = "Conductas", description = "API para gestión de conductas")
public class ConductaApiController {

    private final ConductaService conductaService;

    @GetMapping
    @Operation(summary = "Obtener todas las conductas")
    public ResponseEntity<List<Conducta>> getAllConductas() {
        List<Conducta> conductas = conductaService.findAll();
        return ResponseEntity.ok(conductas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener conducta por ID")
    public ResponseEntity<Conducta> getConductaById(@PathVariable Long id) {
        return conductaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva conducta")
    public ResponseEntity<Conducta> createConducta(@RequestBody Conducta conducta) {
        Conducta savedConducta = conductaService.save(conducta);
        return ResponseEntity.ok(savedConducta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar conducta existente")
    public ResponseEntity<Conducta> updateConducta(@PathVariable Long id, @RequestBody Conducta conducta) {
        if (!conductaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        conducta.setIdConducta(id);
        Conducta updatedConducta = conductaService.save(conducta);
        return ResponseEntity.ok(updatedConducta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar conducta")
    public ResponseEntity<Void> deleteConducta(@PathVariable Long id) {
        if (!conductaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        conductaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/gravedad/{idGravedad}")
    @Operation(summary = "Obtener conductas por gravedad")
    public ResponseEntity<List<Conducta>> getConductasByGravedad(@PathVariable Long idGravedad) {
        List<Conducta> conductas = conductaService.findByGravedadId(idGravedad);
        return ResponseEntity.ok(conductas);
    }
}