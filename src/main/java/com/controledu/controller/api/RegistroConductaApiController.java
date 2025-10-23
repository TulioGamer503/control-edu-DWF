package com.controledu.controller.api;

import com.controledu.dto.RegistroConductaRequest;
import com.controledu.dto.RegistroConductaResponseDTO; // <-- Importa el DTO de respuesta
import com.controledu.model.RegistroConducta;
import com.controledu.service.RegistroConductaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors; // <-- Importante para convertir la lista

@RestController
@RequestMapping("/api/registro-conductas")
@RequiredArgsConstructor
@Tag(name = "Registro de Conductas", description = "API para gestión de registros de conducta")
public class RegistroConductaApiController {

    private final RegistroConductaService registroConductaService;

    // ¡MÉTODO GET CORREGIDO!
    @GetMapping
    @Operation(summary = "Obtener todos los registros de conducta")
    public ResponseEntity<List<RegistroConductaResponseDTO>> getAllRegistros() {
        List<RegistroConductaResponseDTO> registrosDTO = registroConductaService.findAll()
                .stream()
                .map(RegistroConductaResponseDTO::fromEntity) // Convierte cada entidad a DTO
                .collect(Collectors.toList());
        return ResponseEntity.ok(registrosDTO);
    }

    // ¡MÉTODO GET POR ID CORREGIDO!
    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro de conducta por ID")
    public ResponseEntity<RegistroConductaResponseDTO> getRegistroById(@PathVariable Long id) {
        return registroConductaService.findById(id)
                .map(registro -> ResponseEntity.ok(RegistroConductaResponseDTO.fromEntity(registro))) // Convierte a DTO
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo registro de conducta usando IDs")
    public ResponseEntity<RegistroConductaResponseDTO> createRegistroConducta(@RequestBody RegistroConductaRequest request) {
        try {
            RegistroConducta nuevoRegistro = registroConductaService.registrarIncidente(
                    request.getEstudianteId(),
                    request.getConductaId(),
                    request.getDocenteId(),
                    request.getObservaciones()
            );
            return new ResponseEntity<>(RegistroConductaResponseDTO.fromEntity(nuevoRegistro), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ... (El resto de tus métodos también deberían devolver DTOs si es necesario) ...
}