package com.controledu.controller.api;

import com.controledu.model.Observacion;
import com.controledu.service.ObservacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/observaciones")
@RequiredArgsConstructor
@Tag(name = "Observaciones", description = "API para gestión de observaciones")
public class ObservacionApiController {

    private final ObservacionService observacionService;

    @GetMapping
    @Operation(summary = "Obtener todas las observaciones")
    public ResponseEntity<List<Observacion>> getAllObservaciones() {
        List<Observacion> observaciones = observacionService.findAll();
        return ResponseEntity.ok(observaciones);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener observación por ID")
    public ResponseEntity<Observacion> getObservacionById(@PathVariable Long id) {
        return observacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva observación")
    public ResponseEntity<Observacion> createObservacion(@RequestBody Observacion observacion) {
        // --- CORRECCIÓN AQUÍ ---
        Observacion savedObservacion = observacionService.guardar(observacion); // Changed save to guardar
        // -------------------------
        return ResponseEntity.ok(savedObservacion);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar observación existente")
    public ResponseEntity<Observacion> updateObservacion(@PathVariable Long id, @RequestBody Observacion observacion) {
        if (!observacionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        observacion.setIdObservacion(id); // Make sure the ID from the path is set on the object
        // --- CORRECCIÓN AQUÍ ---
        Observacion updatedObservacion = observacionService.guardar(observacion); // Changed save to guardar
        // -------------------------
        return ResponseEntity.ok(updatedObservacion);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar observación")
    public ResponseEntity<Void> deleteObservacion(@PathVariable Long id) {
        if (!observacionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        observacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Obtener observaciones por estudiante")
    public ResponseEntity<List<Observacion>> getObservacionesByEstudiante(@PathVariable Long estudianteId) {
        List<Observacion> observaciones = observacionService.findByEstudianteId(estudianteId);
        return ResponseEntity.ok(observaciones);
    }

    @GetMapping("/docente/{docenteId}")
    @Operation(summary = "Obtener observaciones por docente")
    public ResponseEntity<List<Observacion>> getObservacionesByDocente(@PathVariable Long docenteId) {
        List<Observacion> observaciones = observacionService.findByDocenteId(docenteId);
        return ResponseEntity.ok(observaciones);
    }

    @PatchMapping("/{id}/marcar-leida")
    @Operation(summary = "Marcar observación como leída")
    public ResponseEntity<Observacion> marcarComoLeida(@PathVariable Long id) {
        return observacionService.marcarComoLeida(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}