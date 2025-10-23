package com.controledu.dto;

import lombok.Data;

@Data
public class EstudianteSimpleDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String grado;
    private String seccion;
}