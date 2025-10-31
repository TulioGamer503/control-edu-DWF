package com.controledu.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador general de dashboard.
 *
 * Su propósito es determinar el tipo de usuario autenticado (rol)
 * y redirigirlo automáticamente al panel correspondiente según su perfil.
 *
 * Roles manejados:
 * - DIRECTOR  → /director/dashboard
 * - DOCENTE   → /docente/dashboard
 * - ESTUDIANTE → /estudiante/dashboard
 *
 * Si no hay usuario autenticado o el rol no es reconocido,
 * se redirige al login con un parámetro de error.
 */
@Controller
public class DashboardController {

    /**
     * Punto de entrada principal al dashboard.
     *
     * Este método evalúa la sesión activa y su rol:
     * 1. Verifica si existe un usuario autenticado en la sesión.
     * 2. Obtiene el rol almacenado en sesión.
     * 3. Redirige a la vista del dashboard correspondiente según el rol.
     * 4. Si no hay sesión o el rol no coincide con los esperados,
     *    se devuelve al login.
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        // Verificamos si el usuario está autenticado
        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            // Si no hay usuario en sesión, redirigimos al login
            return "redirect:/auth/login";
        }

        // Obtenemos el rol del usuario guardado en sesión
        String rol = (String) session.getAttribute("rol");

        // Redirigimos según el rol del usuario autenticado
        return switch (rol) {
            case "DIRECTOR" -> "redirect:/director/dashboard";
            case "DOCENTE" -> "redirect:/docente/dashboard";
            case "ESTUDIANTE" -> "redirect:/estudiante/dashboard";
            // En caso de rol desconocido, regresamos al login con error
            default -> "redirect:/auth/login?error";
        };
    }
}
