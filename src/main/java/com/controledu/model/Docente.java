package com.controledu.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "docente")
public class Docente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellidos;
    private String materia;
    private String usuario;
    private String password;

    public String getRol() {
        return "DOCENTE";
    }

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }
}