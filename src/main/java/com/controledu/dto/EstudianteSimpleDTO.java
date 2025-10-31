package com.controledu.dto;

import lombok.Data;

/**
 * DTO simplificado que representa la información básica de un estudiante.
 *
 * Se utiliza para respuestas donde no es necesario incluir todas las relaciones
 * o datos sensibles del estudiante, por ejemplo, al listar registros de conducta
 * o observaciones.
 *
 * Campos principales:
 * - id: Identificador único del estudiante.
 * - nombres: Nombres del estudiante.
 * - apellidos: Apellidos del estudiante.
 * - grado: Grado académico al que pertenece.
 * - seccion: Sección del estudiante dentro del grado.
 */
@Data
public class EstudianteSimpleDTO {

    /** Identificador único del estudiante */
    private Long id;

    /** Nombres del estudiante */
    private String nombres;

    /** Apellidos del estudiante */
    private String apellidos;

    /** Grado académico del estudiante (ej. 9°, 10°, etc.) */
    private String grado;

    /** Sección dentro del grado (ej. A, B, C) */
    private String seccion;
}
