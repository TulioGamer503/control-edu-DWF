package com.controledu.controller.api;

import com.controledu.dto.RegistroConductaRequest;
import com.controledu.dto.RegistroConductaResponseDTO; // DTO de respuesta
import com.controledu.model.RegistroConducta;
import com.controledu.service.RegistroConductaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors; // Necesario para mapear listas de entidades a DTOs

/**
 * Controlador REST para la gestión de registros de conducta (incidentes) en la aplicación.
 *
 * Expone endpoints bajo el prefijo /api/registro-conductas.
 * Utiliza DTOs para evitar exponer directamente las entidades JPA.
 * Documentado con Swagger para integración con OpenAPI UI.
 */
@RestController
@RequestMapping("/api/registro-conductas")
@RequiredArgsConstructor
@Tag(name = "Registro de Conductas", description = "API para gestión de registros de conducta")
public class RegistroConductaApiController {

    private final RegistroConductaService registroConductaService;

    // ================================================================
    // GET: Obtener todos los registros de conducta
    // ================================================================

    /**
     * Devuelve la lista completa de registros de conducta existentes en el sistema.
     * Convierte cada entidad a un DTO antes de enviarla en la respuesta.
     *
     * @return Lista de registros en formato DTO con código 200 OK.
     */
    @GetMapping
    @Operation(summary = "Obtener todos los registros de conducta")
    public ResponseEntity<List<RegistroConductaResponseDTO>> getAllRegistros() {
        // Llama al servicio para obtener todas las entidades y las convierte a DTO
        List<RegistroConductaResponseDTO> registrosDTO = registroConductaService.findAll()
                .stream()
                .map(RegistroConductaResponseDTO::fromEntity) // Conversión entidad → DTO
                .collect(Collectors.toList());

        return ResponseEntity.ok(registrosDTO);
    }

    // ================================================================
    // GET: Obtener registro de conducta por ID
    // ================================================================

    /**
     * Obtiene un registro de conducta específico según su identificador único.
     * Si no se encuentra, devuelve 404 NOT FOUND.
     *
     * @param id Identificador del registro de conducta.
     * @return Registro de conducta en formato DTO o 404 si no existe.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro de conducta por ID")
    public ResponseEntity<RegistroConductaResponseDTO> getRegistroById(@PathVariable Long id) {
        return registroConductaService.findById(id)
                .map(registro -> ResponseEntity.ok(RegistroConductaResponseDTO.fromEntity(registro))) // Mapeo entidad → DTO
                .orElse(ResponseEntity.notFound().build()); // 404 si no existe
    }

    // ================================================================
    // POST: Crear nuevo registro de conducta
    // ================================================================

    /**
     * Crea un nuevo registro de conducta a partir de un DTO de solicitud.
     * El DTO debe incluir los IDs de estudiante, docente y conducta.
     * En caso de error en la validación o relación, devuelve 400 BAD REQUEST.
     *
     * @param request Objeto con los datos para crear el registro de conducta.
     * @return DTO del registro creado y código 201 CREATED.
     */
    @PostMapping
    @Operation(summary = "Crear nuevo registro de conducta usando IDs")
    public ResponseEntity<RegistroConductaResponseDTO> createRegistroConducta(@RequestBody RegistroConductaRequest request) {
        try {
            // Se delega al servicio la lógica de validación y persistencia
            RegistroConducta nuevoRegistro = registroConductaService.registrarIncidente(
                    request.getEstudianteId(),
                    request.getConductaId(),
                    request.getDocenteId(),
                    request.getObservaciones()
            );

            // Devuelve el nuevo registro convertido a DTO con estado 201 Created
            return new ResponseEntity<>(RegistroConductaResponseDTO.fromEntity(nuevoRegistro), HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Error genérico: puede ser por entidades no encontradas o datos inválidos
            return ResponseEntity.badRequest().build();
        }
    }
}
