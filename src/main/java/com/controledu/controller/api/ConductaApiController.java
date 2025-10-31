package com.controledu.controller.api;

import com.controledu.model.Conducta;
import com.controledu.service.ConductaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de conductas (reglas de comportamiento).
 *
 * Expone endpoints CRUD y consultas adicionales como filtrado por nivel de gravedad.
 *
 * Ruta base: /api/conductas
 *
 * Buenas prácticas aplicadas:
 * - Uso de ResponseEntity para respuestas con códigos HTTP correctos.
 * - Verificación de existencia antes de actualizar o eliminar.
 * - Documentación con Swagger/OpenAPI para autogenerar la especificación de la API.
 */
@RestController
@RequestMapping("/api/conductas")
@RequiredArgsConstructor
@Tag(name = "Conductas", description = "API para gestión de conductas")
public class ConductaApiController {

    private final ConductaService conductaService;

    // ================================================================
    // GET: Listar todas las conductas
    // ================================================================
    /**
     * Devuelve una lista de todas las conductas registradas.
     *
     * @return Lista de objetos Conducta con código 200 OK.
     */
    @GetMapping
    @Operation(summary = "Obtener todas las conductas")
    public ResponseEntity<List<Conducta>> getAllConductas() {
        List<Conducta> conductas = conductaService.findAll();
        return ResponseEntity.ok(conductas);
    }

    // ================================================================
    // GET: Obtener conducta por ID
    // ================================================================
    /**
     * Busca una conducta específica por su identificador único.
     * Si no se encuentra, devuelve un código 404 Not Found.
     *
     * @param id Identificador de la conducta.
     * @return Conducta encontrada o respuesta 404 si no existe.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener conducta por ID")
    public ResponseEntity<Conducta> getConductaById(@PathVariable Long id) {
        return conductaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ================================================================
    // POST: Crear nueva conducta
    // ================================================================
    /**
     * Crea una nueva conducta en la base de datos.
     *
     * @param conducta Objeto Conducta con los datos a registrar.
     * @return Conducta creada con código 200 OK.
     */
    @PostMapping
    @Operation(summary = "Crear nueva conducta")
    public ResponseEntity<Conducta> createConducta(@RequestBody Conducta conducta) {
        Conducta savedConducta = conductaService.save(conducta);
        return ResponseEntity.ok(savedConducta);
    }

    // ================================================================
    // PUT: Actualizar conducta existente
    // ================================================================
    /**
     * Actualiza una conducta existente.
     * Si no existe, devuelve un código 404.
     *
     * @param id ID de la conducta a actualizar.
     * @param conducta Objeto con los nuevos valores.
     * @return Conducta actualizada o 404 si no se encuentra.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar conducta existente")
    public ResponseEntity<Conducta> updateConducta(@PathVariable Long id, @RequestBody Conducta conducta) {
        if (!conductaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Garantiza que el ID de la entidad corresponda al de la URL
        conducta.setIdConducta(id);
        Conducta updatedConducta = conductaService.save(conducta);
        return ResponseEntity.ok(updatedConducta);
    }

    // ================================================================
    // DELETE: Eliminar conducta
    // ================================================================
    /**
     * Elimina una conducta existente por su ID.
     * Devuelve 204 No Content si se elimina correctamente, o 404 si no existe.
     *
     * @param id ID de la conducta a eliminar.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar conducta")
    public ResponseEntity<Void> deleteConducta(@PathVariable Long id) {
        if (!conductaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        conductaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ================================================================
    // GET: Filtrar conductas por nivel de gravedad
    // ================================================================
    /**
     * Obtiene todas las conductas asociadas a un nivel de gravedad específico.
     *
     * @param idGravedad Identificador del nivel de gravedad.
     * @return Lista de conductas filtradas con código 200 OK.
     */
    @GetMapping("/gravedad/{idGravedad}")
    @Operation(summary = "Obtener conductas por gravedad")
    public ResponseEntity<List<Conducta>> getConductasByGravedad(@PathVariable Long idGravedad) {
        List<Conducta> conductas = conductaService.findByGravedadId(idGravedad);
        return ResponseEntity.ok(conductas);
    }
}
