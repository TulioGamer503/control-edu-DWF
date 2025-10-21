package com.controledu.controller;

import com.controledu.model.Docente;
import com.controledu.service.RegistroConductaService;
import com.controledu.service.ObservacionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/docente")
@RequiredArgsConstructor
public class DocenteController {

    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }

        // Estadísticas del docente
        long totalIncidentes = registroConductaService.countByDocenteId(docente.getId());
        long totalObservaciones = observacionService.countByDocenteId(docente.getId());

        model.addAttribute("docente", docente);
        model.addAttribute("totalIncidentes", totalIncidentes);
        model.addAttribute("totalObservaciones", totalObservaciones);

        return "docente/dashboard";
    }
}