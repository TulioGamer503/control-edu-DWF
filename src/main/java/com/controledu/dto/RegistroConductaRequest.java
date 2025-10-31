package com.controledu.dto;

import lombok.Data;

/**
 * DTO para la creación de un nuevo Registro de Conducta.
 * Contiene únicamente los identificadores necesarios para establecer las relaciones
 * (estudiante, docente y conducta) y el texto libre de observaciones que describe el incidente.
 *
 * Uso típico (capa API):
 *  - Se recibe como cuerpo JSON en el endpoint POST de registros de conducta.
 *  - La capa de servicio resuelve las entidades por ID y persiste el registro.
 *
 * Validación sugerida (a realizar en Controller/Service o con anotaciones):
 *  - estudianteId, docenteId, conductaId: requeridos y > 0
 *  - observaciones: opcional o con longitud mínima/máxima según reglas del dominio
 */
@Data
public class RegistroConductaRequest {

    /** ID del estudiante involucrado en el registro de conducta */
    private Long estudianteId;

    /** ID del docente que reporta o registra la incidencia */
    private Long docenteId;

    /** ID de la conducta (regla) asociada al incidente */
    private Long conductaId;

    /** Descripción u observaciones del incidente (texto libre) */
    // Este es el campo que te faltaba
    private String observaciones;

}
