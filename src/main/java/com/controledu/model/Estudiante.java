package com.controledu.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "estudiante")
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellidos;
    private String grado;
    private String seccion;
    private LocalDate fechaNacimiento;
    private String usuario;
    private String password;

    public String getRol() {
        return "ESTUDIANTE";
    }

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }

    public String getGradoSeccion() {
        return this.grado + "° " + this.seccion;
    }

    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
}