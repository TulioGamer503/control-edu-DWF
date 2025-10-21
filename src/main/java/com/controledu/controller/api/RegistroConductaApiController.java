package com.controledu.controller.api;

import com.controledu.model.RegistroConducta;
import com.controledu.service.RegistroConductaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/registro-conductas")
@RequiredArgsConstructor
@Tag(name = "Registro de Conductas", description = "API para gestión de registros de conducta")
public class RegistroConductaApiController {

    private final RegistroConductaService registroConductaService;

    @GetMapping
    @Operation(summary = "Obtener todos los registros de conducta")
    public ResponseEntity<List<RegistroConducta>> getAllRegistros() {
        List<RegistroConducta> registros = registroConductaService.findAll();
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro de conducta por ID")
    public ResponseEntity<RegistroConducta> getRegistroById(@PathVariable Long id) {
        return registroConductaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo registro de conducta")
    public ResponseEntity<RegistroConducta> createRegistroConducta(@RequestBody RegistroConducta registroConducta) {
        RegistroConducta savedRegistro = registroConductaService.save(registroConducta);
        return ResponseEntity.ok(savedRegistro);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro de conducta existente")
    public ResponseEntity<RegistroConducta> updateRegistroConducta(@PathVariable Long id, @RequestBody RegistroConducta registroConducta) {
        if (!registroConductaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        registroConducta.setIdRegistro(id);
        RegistroConducta updatedRegistro = registroConductaService.save(registroConducta);
        return ResponseEntity.ok(updatedRegistro);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro de conducta")
    public ResponseEntity<Void> deleteRegistroConducta(@PathVariable Long id) {
        if (!registroConductaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        registroConductaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Obtener registros por estudiante")
    public ResponseEntity<List<RegistroConducta>> getRegistrosByEstudiante(@PathVariable Long estudianteId) {
        List<RegistroConducta> registros = registroConductaService.findByEstudianteId(estudianteId);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/docente/{docenteId}")
    @Operation(summary = "Obtener registros por docente")
    public ResponseEntity<List<RegistroConducta>> getRegistrosByDocente(@PathVariable Long docenteId) {
        List<RegistroConducta> registros = registroConductaService.findByDocenteId(docenteId);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/fecha")
    @Operation(summary = "Obtener registros por fecha")
    public ResponseEntity<List<RegistroConducta>> getRegistrosByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<RegistroConducta> registros = registroConductaService.findByFecha(fecha);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/rango-fechas")
    @Operation(summary = "Obtener registros por rango de fechas")
    public ResponseEntity<List<RegistroConducta>> getRegistrosByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<RegistroConducta> registros = registroConductaService.findByRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(registros);
    }

    @PatchMapping("/{id}/marcar-leido")
    @Operation(summary = "Marcar registro como leído")
    public ResponseEntity<RegistroConducta> marcarComoLeido(@PathVariable Long id) {
        return registroConductaService.marcarComoLeido(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estadisticas/gravedad")
    @Operation(summary = "Obtener estadísticas por gravedad")
    public ResponseEntity<List<Object[]>> getEstadisticasPorGravedad() {
        List<Object[]> estadisticas = registroConductaService.countByGravedad();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/estadisticas/grado")
    @Operation(summary = "Obtener estadísticas por grado")
    public ResponseEntity<List<Object[]>> getEstadisticasPorGrado() {
        List<Object[]> estadisticas = registroConductaService.countByGrado();
        return ResponseEntity.ok(estadisticas);
    }
}