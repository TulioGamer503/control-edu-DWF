package com.controledu.dto;

import lombok.Data;

/**
 * DTO utilizado para recibir los datos de autenticación de un usuario.
 *
 * Este objeto se envía desde el cliente al servidor durante el proceso de login
 * para validar las credenciales del usuario.
 *
 * Campos:
 * - usuario: Nombre de usuario o credencial usada para iniciar sesión.
 * - password: Contraseña correspondiente al usuario.
 */
@Data
public class LoginRequest {

    /** Nombre de usuario o identificador de acceso */
    private String usuario;

    /** Contraseña del usuario */
    private String password;
}
