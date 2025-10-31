package com.controledu.controller;

import com.controledu.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador responsable de flujos de autenticación y gestión básica de perfil.
 * Agrupa endpoints bajo el prefijo /auth (login, logout, acceso denegado, perfil, cambio de contraseña).
 *
 * Notas de seguridad/arquitectura:
 * - La autenticación real (verificación de credenciales, obtención de rol y actualización de perfil)
 *   se delega en AuthService.
 * - Se usa HttpSession para persistir datos mínimos del usuario autenticado (usuario, rol).
 * - Las vistas devueltas corresponden a plantillas Thymeleaf (u otro motor) bajo /auth/*.
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /** Servicio que encapsula la lógica de autenticación, roles y actualización de perfil.*/
    private final AuthService authService;

    /**  Métodos existentes: /login (GET y POST), /logout, /access-denied*/

    /**
     * Muestra el formulario de login.
     * Si viene "error", se muestra un mensaje de credenciales inválidas.
     * Si viene "logout", se informa que la sesión fue cerrada.
     */
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión exitosamente");
        }
        /** Retorna la vista del formulario de autenticación.*/
        return "auth/login";
    }

    /**
     * Procesa el inicio de sesión.
     * - Recibe usuario y password desde el formulario.
     * - Autentica usando AuthService.authenticate().
     * - Si es válido, guarda en sesión el objeto usuario y su rol.
     * - Redirige a dashboard según el rol; si no coincide, retorna a login con error.
     */
    @PostMapping("/login")
    public String login(@RequestParam String usuario,
                        @RequestParam String password,
                        HttpSession session,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        /**  Autenticación contra el servicio (devuelve null si falla).*/
        Object user = authService.authenticate(usuario, password);

        if (user != null) {
            /** Persistimos en sesión el usuario autenticado y su rol.*/
            session.setAttribute("usuario", user);
            session.setAttribute("rol", authService.getUserRole(user));

            /** Redirección basada en rol del usuario autenticado.*/
            String rol = authService.getUserRole(user);
            return switch (rol) {
                case "DIRECTOR" -> "redirect:/director/dashboard";
                case "DOCENTE" -> "redirect:/docente/dashboard";
                case "ESTUDIANTE" -> "redirect:/estudiante/dashboard";
                default -> "redirect:/auth/login?error";
            };
        } else {
            // Si la autenticación falla, se informa en la misma vista de login.*/
            model.addAttribute("error", "Credenciales inválidas");
            model.addAttribute("usuario", usuario);
            return "auth/login";
        }
    }

    /**
     * Cierra la sesión actual.
     * - Invalida la HttpSession.
     * - Usa flash attribute para mostrar mensaje en el redirect.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "Sesión cerrada exitosamente");
        return "redirect:/auth/login";
    }

    /**
     * Vista de acceso denegado (403).
     * - Útil cuando hay filtros/seguridad que bloquean un recurso por falta de permisos.
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }

    /**
     * Muestra el perfil del usuario autenticado.
     * - Si no hay usuario en sesión, redirige a login.
     * - Expone en el modelo el objeto "usuario" y "director" (misma referencia).
     */
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("director", usuario);
        return "auth/profile";
    }

    // --- RUTA DE EDICIÓN DE PERFIL ---*/

    /**
     * Muestra el formulario para editar el perfil.
     * - Requiere usuario autenticado (en sesión).
     * - Carga los datos actuales en el modelo para rellenar el formulario.
     */
    @GetMapping("/profile/edit")
    public String showEditProfileForm(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("director", usuario);
        return "auth/edit-profile";
    }

    /**
     * Recibe el POST de edición de perfil.
     * - Valida que exista un usuario en sesión.
     * - Llama a AuthService.updateUserProfile(...) para persistir cambios.
     * - Actualiza el objeto en sesión y redirige al perfil con mensaje de éxito.
     * - Si ocurre una excepción, redirige nuevamente al formulario con error.
     *
     * Nota: Si se dispone de un DTO, podría usarse @ModelAttribute en lugar de params sueltos.
     */
    @PostMapping("/profile/edit")
    public String editProfile(@RequestParam String nombres,
                              @RequestParam String apellidos,
                              // Agrega aquí otros campos específicos (materia, grado, etc.) si es necesario,
                              // o usa @ModelAttribute si tienes un DTO
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Object currentUsuario = session.getAttribute("usuario");
        if (currentUsuario == null) {
            return "redirect:/auth/login";
        }

        try {
            // Actualización del perfil a través del servicio.
            Object updatedUser = authService.updateUserProfile(currentUsuario, nombres, apellidos);

            // Refrescamos el usuario en la sesión con los datos persistidos.
            session.setAttribute("usuario", updatedUser);

            redirectAttributes.addFlashAttribute("success", "¡Perfil actualizado con éxito!");
            return "redirect:/auth/profile";

        } catch (Exception e) {
            // Si la capa de servicio/DB lanza error, se informa y se retorna al formulario.
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/auth/profile/edit";
        }
    }

    // --- RUTA DE CAMBIO DE CONTRASEÑA ---

    /**
     * Muestra el formulario de cambio de contraseña.
     * - Requiere usuario autenticado.
     */
    @GetMapping("/profile/password")
    public String showChangePasswordForm(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("director", usuario);
        return "auth/change-password";
    }

    /**
     * Procesa el cambio de contraseña.
     * - Verifica existencia de usuario en sesión.
     * - Comprueba que newPassword y confirmPassword coincidan.
     * - Delegado a AuthService.changePassword(...) para la lógica de validación/actualización.
     * - Si tiene éxito, invalida la sesión por seguridad y redirige a login.
     * - En caso de error, retorna al formulario con mensaje descriptivo.
     */
    @PostMapping("/profile/password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Object usuario = session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/auth/login";
        }

        // Validación básica: coincidencia de nueva contraseña y confirmación.
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "La nueva contraseña y su confirmación no coinciden.");
            return "redirect:/auth/profile/password";
        }

        try {
            // Lógica de cambio de contraseña delegada al servicio.
            authService.changePassword(usuario, currentPassword, newPassword);

            // Buenas prácticas: invalidar la sesión vigente tras cambiar credenciales.
            session.invalidate();
            redirectAttributes.addFlashAttribute("message", "Contraseña actualizada con éxito. Por favor, inicia sesión de nuevo.");
            return "redirect:/auth/login";

        } catch (Exception e) {
            // Si el servicio lanza excepción (ej. contraseña actual incorrecta), se notifica al usuario.
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/auth/profile/password";
        }
    }
}
