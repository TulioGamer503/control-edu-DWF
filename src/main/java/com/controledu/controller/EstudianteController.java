package com.controledu.controller;

import com.controledu.model.Estudiante;
import com.controledu.service.RegistroConductaService;
import com.controledu.service.ObservacionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/estudiante")
@RequiredArgsConstructor
public class EstudianteController {

    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            return "redirect:/auth/login";
        }

        // Estadísticas del estudiante
        long totalFaltas = registroConductaService.countByEstudianteId(estudiante.getId());
        long totalObservaciones = observacionService.countByEstudianteId(estudiante.getId());

        model.addAttribute("estudiante", estudiante);
        model.addAttribute("totalFaltas", totalFaltas);
        model.addAttribute("totalObservaciones", totalObservaciones);

        return "estudiante/dashboard";
    }
}