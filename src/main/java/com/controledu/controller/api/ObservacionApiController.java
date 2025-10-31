package com.controledu.controller.api;

import com.controledu.model.Observacion;
import com.controledu.service.ObservacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de observaciones.
 *
 * Expone operaciones CRUD y consultas específicas (por estudiante y por docente),
 * además de una acción parcial para marcar observaciones como leídas.
 *
 * Endpoints base: /api/observaciones
 *
 * Buenas prácticas aplicadas:
 * - Uso de ResponseEntity para controlar códigos HTTP (200, 201, 204, 404).
 * - Verificación de existencia antes de actualizar o eliminar.
 * - Documentación mediante anotaciones Swagger/OpenAPI.
 */
@RestController
@RequestMapping("/api/observaciones")
@RequiredArgsConstructor
@Tag(name = "Observaciones", description = "API para gestión de observaciones")
public class ObservacionApiController {

    private final ObservacionService observacionService;

    // ================================================================
    // GET: Listar todas las observaciones
    // ================================================================
    /**
     * Obtiene todas las observaciones registradas en la base de datos.
     *
     * @return Lista de objetos Observacion con código 200 OK.
     */
    @GetMapping
    @Operation(summary = "Obtener todas las observaciones")
    public ResponseEntity<List<Observacion>> getAllObservaciones() {
        List<Observacion> observaciones = observacionService.findAll();
        return ResponseEntity.ok(observaciones);
    }

    // ================================================================
    // GET: Obtener observación por ID
    // ================================================================
    /**
     * Recupera una observación específica mediante su ID.
     * Si no se encuentra, devuelve 404 Not Found.
     *
     * @param id ID de la observación.
     * @return Objeto Observacion si existe, o 404 si no.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener observación por ID")
    public ResponseEntity<Observacion> getObservacionById(@PathVariable Long id) {
        return observacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ================================================================
    // POST: Crear nueva observación
    // ================================================================
    /**
     * Crea una nueva observación en el sistema.
     * El cuerpo de la solicitud debe incluir los datos completos de la observación.
     *
     * @param observacion Objeto con los datos a registrar.
     * @return Observación guardada con código 200 OK.
     */
    @PostMapping
    @Operation(summary = "Crear nueva observación")
    public ResponseEntity<Observacion> createObservacion(@RequestBody Observacion observacion) {
        // Uso del método guardar definido en el servicio
        Observacion savedObservacion = observacionService.guardar(observacion);
        return ResponseEntity.ok(savedObservacion);
    }

    // ================================================================
    // PUT: Actualizar observación existente
    // ================================================================
    /**
     * Actualiza una observación existente. Si no se encuentra, devuelve 404.
     * Se asegura de que el ID proporcionado en la URL prevalezca sobre el del cuerpo.
     *
     * @param id ID de la observación a actualizar.
     * @param observacion Datos actualizados.
     * @return Observación actualizada con código 200 OK o 404 si no existe.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar observación existente")
    public ResponseEntity<Observacion> updateObservacion(@PathVariable Long id, @RequestBody Observacion observacion) {
        if (!observacionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        observacion.setIdObservacion(id); // Asegura coherencia entre URL y entidad
        Observacion updatedObservacion = observacionService.guardar(observacion);
        return ResponseEntity.ok(updatedObservacion);
    }

    // ================================================================
    // DELETE: Eliminar observación
    // ================================================================
    /**
     * Elimina una observación específica según su ID.
     * Si no existe, devuelve 404. Si se elimina correctamente, responde con 204 No Content.
     *
     * @param id ID de la observación a eliminar.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar observación")
    public ResponseEntity<Void> deleteObservacion(@PathVariable Long id) {
        if (!observacionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        observacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ================================================================
    // GET: Observaciones por estudiante
    // ================================================================
    /**
     * Obtiene todas las observaciones asociadas a un estudiante.
     *
     * @param estudianteId ID del estudiante.
     * @return Lista de observaciones relacionadas con ese estudiante.
     */
    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Obtener observaciones por estudiante")
    public ResponseEntity<List<Observacion>> getObservacionesByEstudiante(@PathVariable Long estudianteId) {
        List<Observacion> observaciones = observacionService.findByEstudianteId(estudianteId);
        return ResponseEntity.ok(observaciones);
    }

    // ================================================================
    // GET: Observaciones por docente
    // ================================================================
    /**
     * Obtiene todas las observaciones registradas por un docente.
     *
     * @param docenteId ID del docente.
     * @return Lista de observaciones hechas por ese docente.
     */
    @GetMapping("/docente/{docenteId}")
    @Operation(summary = "Obtener observaciones por docente")
    public ResponseEntity<List<Observacion>> getObservacionesByDocente(@PathVariable Long docenteId) {
        List<Observacion> observaciones = observacionService.findByDocenteId(docenteId);
        return ResponseEntity.ok(observaciones);
    }

    // ================================================================
    // PATCH: Marcar observación como leída
    // ================================================================
    /**
     * Marca una observación específica como leída.
     * Devuelve la observación actualizada o 404 si no existe.
     *
     * @param id ID de la observación a actualizar.
     * @return Observación marcada como leída o 404.
     */
    @PatchMapping("/{id}/marcar-leida")
    @Operation(summary = "Marcar observación como leída")
    public ResponseEntity<Observacion> marcarComoLeida(@PathVariable Long id) {
        return observacionService.marcarComoLeida(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
