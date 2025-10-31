package com.controledu.dto;

import lombok.Data;

/**
 * DTO que representa la respuesta enviada al cliente después
 * de un proceso de autenticación exitoso.
 *
 * Incluye tanto el token de acceso (JWT) como la información básica
 * del usuario autenticado para facilitar su identificación en el frontend.
 */
@Data
public class AuthResponse {

    /** Token JWT generado tras la autenticación */
    private String token;

    /** Tipo de token, comúnmente “Bearer” para encabezados Authorization */
    private String tipo = "Bearer";

    /** Identificador único del usuario autenticado */
    private Long id;

    /** Nombre de usuario (credencial usada para autenticarse) */
    private String usuario;

    /** Nombres del usuario */
    private String nombres;

    /** Apellidos del usuario */
    private String apellidos;

    /** Rol del usuario dentro del sistema (DIRECTOR, DOCENTE, ESTUDIANTE, etc.) */
    private String rol;

    /**
     * Constructor completo que inicializa la respuesta con todos los campos relevantes.
     *
     * @param token Token JWT asignado al usuario.
     * @param id Identificador del usuario.
     * @param usuario Nombre de usuario utilizado para iniciar sesión.
     * @param nombres Nombres personales del usuario.
     * @param apellidos Apellidos personales del usuario.
     * @param rol Rol del usuario dentro del sistema.
     */
    public AuthResponse(String token, Long id, String usuario, String nombres, String apellidos, String rol) {
        this.token = token;
        this.id = id;
        this.usuario = usuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.rol = rol;
    }
}
