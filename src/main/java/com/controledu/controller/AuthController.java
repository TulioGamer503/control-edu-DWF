package com.controledu.controller;

import com.controledu.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Métodos existentes: /login (GET y POST), /logout, /access-denied

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
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String usuario,
                        @RequestParam String password,
                        HttpSession session,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        Object user = authService.authenticate(usuario, password);

        if (user != null) {
            session.setAttribute("usuario", user);
            session.setAttribute("rol", authService.getUserRole(user));

            // Redirigir según el rol
            String rol = authService.getUserRole(user);
            return switch (rol) {
                case "DIRECTOR" -> "redirect:/director/dashboard";
                case "DOCENTE" -> "redirect:/docente/dashboard";
                case "ESTUDIANTE" -> "redirect:/estudiante/dashboard";
                default -> "redirect:/auth/login?error";
            };
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            model.addAttribute("usuario", usuario);
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "Sesión cerrada exitosamente");
        return "redirect:/auth/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }

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

    // --- RUTA DE EDICIÓN DE PERFIL ---

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

    // AÑADIDO: Método POST para recibir los datos del formulario de edición.
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
            // Llama al servicio para actualizar los datos.
            // La implementación real de `updateUserProfile` está en AuthService.
            Object updatedUser = authService.updateUserProfile(currentUsuario, nombres, apellidos);

            // Actualiza el objeto en la sesión
            session.setAttribute("usuario", updatedUser);

            redirectAttributes.addFlashAttribute("success", "¡Perfil actualizado con éxito!");
            return "redirect:/auth/profile";

        } catch (Exception e) {
            // Manejo de error si la actualización falla (ej. error de DB o validación)
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/auth/profile/edit";
        }
    }

    // --- RUTA DE CAMBIO DE CONTRASEÑA ---

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

    // AÑADIDO: Método POST para recibir los datos del formulario de cambio de contraseña.
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

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "La nueva contraseña y su confirmación no coinciden.");
            return "redirect:/auth/profile/password";
        }

        try {
            // Llama al servicio para cambiar la contraseña.
            // La implementación real de `changePassword` está en AuthService.
            authService.changePassword(usuario, currentPassword, newPassword);

            // Invalida la sesión por seguridad después del cambio exitoso
            session.invalidate();
            redirectAttributes.addFlashAttribute("message", "Contraseña actualizada con éxito. Por favor, inicia sesión de nuevo.");
            return "redirect:/auth/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/auth/profile/password";
        }
    }
}