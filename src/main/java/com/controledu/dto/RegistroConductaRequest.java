package com.controledu.dto;

import lombok.Data;

/**
 * DTO para la creación de un nuevo Registro de Conducta.
 * Contiene solo los campos necesarios que se envían en la petición.
 */
@Data
public class RegistroConductaRequest {

    private Long estudianteId;

    private Long docenteId;

    private Long conductaId;

    // Este es el campo que te faltaba
    private String observaciones;

}