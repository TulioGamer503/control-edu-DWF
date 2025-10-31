package com.controledu.dto;

import com.controledu.model.RegistroConducta;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO de respuesta para exponer los registros de conducta a través de la API.
 *
 * Este objeto transforma la entidad {@link RegistroConducta} en una representación
 * más limpia y ligera para el consumo del cliente (por ejemplo, aplicaciones frontend o móviles).
 *
 * Incluye información anidada de estudiante, docente y conducta, con DTOs simplificados.
 */
@Data
public class RegistroConductaResponseDTO {

    /** Identificador único del registro de conducta */
    private Long idRegistro;

    /** Datos básicos del estudiante involucrado */
    private EstudianteSimpleDTO estudiante;

    /** Nombre completo del docente que reportó el incidente */
    private String docenteNombreCompleto;

    /** Datos básicos de la conducta asociada al registro */
    private ConductaSimpleDTO conducta;

    /** Fecha en que se registró la conducta */
    private LocalDate fechaRegistro;

    /** Observaciones o comentarios sobre el incidente */
    private String observaciones;

    /** Estado del registro (por ejemplo: ACTIVO, RESUELTO) */
    private String estado;

    /** Indica si el registro ya fue leído o revisado */
    private Boolean leido;

    /**
     * Convierte una entidad {@link RegistroConducta} en su representación DTO.
     * Este método elimina las referencias circulares y reduce la carga de datos anidados.
     *
     * @param registro Entidad RegistroConducta proveniente de la base de datos
     * @return Objeto {@link RegistroConductaResponseDTO} listo para enviarse al cliente
     */
    public static RegistroConductaResponseDTO fromEntity(RegistroConducta registro) {
        RegistroConductaResponseDTO dto = new RegistroConductaResponseDTO();

        // Datos base
        dto.setIdRegistro(registro.getIdRegistro());
        dto.setFechaRegistro(registro.getFechaRegistro());
        dto.setObservaciones(registro.getObservaciones());
        dto.setEstado(registro.getEstado());
        dto.setLeido(registro.getLeido());

        // --- Mapeo del estudiante ---
        if (registro.getEstudiante() != null) {
            EstudianteSimpleDTO estDto = new EstudianteSimpleDTO();
            estDto.setId(registro.getEstudiante().getId());
            estDto.setNombres(registro.getEstudiante().getNombres());
            estDto.setApellidos(registro.getEstudiante().getApellidos());
            estDto.setGrado(registro.getEstudiante().getGrado() + "°");
            estDto.setSeccion(registro.getEstudiante().getSeccion());
            dto.setEstudiante(estDto);
        }

        // --- Mapeo del docente ---
        if (registro.getDocente() != null) {
            dto.setDocenteNombreCompleto(
                    registro.getDocente().getNombres() + " " + registro.getDocente().getApellidos()
            );
        }

        // --- Mapeo de la conducta ---
        if (registro.getConducta() != null) {
            ConductaSimpleDTO conDto = new ConductaSimpleDTO();
            conDto.setNombreConducta(registro.getConducta().getNombreConducta());

            if (registro.getConducta().getGravedad() != null) {
                conDto.setGravedad(registro.getConducta().getGravedad().getNombreGravedad());
            }

            dto.setConducta(conDto);
        }

        return dto;
    }
}
