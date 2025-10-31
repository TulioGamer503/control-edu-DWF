package com.controledu.dto;

import lombok.Data;

/**
 * DTO simplificado para representar una conducta sin exponer toda la entidad completa.
 *
 * Este objeto se utiliza en respuestas donde solo se necesita mostrar información básica
 * de la conducta y su nivel de gravedad, sin incluir relaciones o datos adicionales.
 *
 * Campos:
 * - nombreConducta: Nombre o descripción corta de la conducta.
 * - gravedad: Nivel de gravedad asociado (por ejemplo, "Leve", "Grave", "Muy Grave").
 */
@Data
public class ConductaSimpleDTO {

    /** Nombre o descripción breve de la conducta */
    private String nombreConducta;

    /** Nombre del nivel de gravedad (solo el texto, no la entidad completa) */
    private String gravedad;
}
