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
        return "auth/profile";
    }
}