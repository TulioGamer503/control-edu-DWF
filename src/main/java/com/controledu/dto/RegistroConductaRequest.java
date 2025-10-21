package com.controledu.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistroConductaRequest {
    private Long estudianteId;
    private Long docenteId;
    private Long conductaId;
    private LocalDate fechaRegistro;
    private String accionesTomadas;
    private String comentarios;
    private String evidenciaUrl;
}