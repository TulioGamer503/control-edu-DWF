package com.controledu.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "nombres", nullable = false, length = 100)
    protected String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    protected String apellidos;

    @Column(name = "usuario", nullable = false, unique = true, length = 50)
    protected String usuario;

    @Column(name = "password", nullable = false, length = 255)
    protected String password;

    @Column(name = "rol", nullable = false, length = 20)
    protected String rol;
}