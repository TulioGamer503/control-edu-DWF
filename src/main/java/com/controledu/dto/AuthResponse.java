package com.controledu.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String usuario;
    private String nombres;
    private String apellidos;
    private String rol;

    public AuthResponse(String token, Long id, String usuario, String nombres, String apellidos, String rol) {
        this.token = token;
        this.id = id;
        this.usuario = usuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.rol = rol;
    }
}