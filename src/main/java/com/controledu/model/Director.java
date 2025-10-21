package com.controledu.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "director")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellidos;
    private String usuario;
    private String password;

    // Método para obtener rol
    public String getRol() {
        return "DIRECTOR";
    }

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }
}