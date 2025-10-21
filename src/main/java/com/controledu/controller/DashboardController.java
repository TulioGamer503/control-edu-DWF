package com.controledu.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        String rol = (String) session.getAttribute("rol");
        return switch (rol) {
            case "DIRECTOR" -> "redirect:/director/dashboard";
            case "DOCENTE" -> "redirect:/docente/dashboard";
            case "ESTUDIANTE" -> "redirect:/estudiante/dashboard";
            default -> "redirect:/auth/login?error";
        };
    }
}