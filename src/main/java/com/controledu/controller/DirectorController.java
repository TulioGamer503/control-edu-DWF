package com.controledu.controller;

import com.controledu.model.Director;
import com.controledu.model.Observacion; // <-- Import para Observacion (¡Necesario!)
import com.controledu.model.RegistroConducta;
import com.controledu.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List; // <-- Import para List (¡Necesario!)

@Controller
@RequestMapping("/director")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;
    private final DocenteService docenteService;
    private final EstudianteService estudianteService;
    private final ConductaService conductaService;
    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService; // <-- Asegúrate que esté aquí

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        // Estadísticas básicas
        long totalEstudiantes = estudianteService.count();
        long totalDocentes = docenteService.count();
        long totalIncidentes = registroConductaService.count();
        long totalObservaciones = observacionService.count();

        model.addAttribute("director", director);
        model.addAttribute("totalEstudiantes", totalEstudiantes);
        model.addAttribute("totalDocentes", totalDocentes);
        model.addAttribute("totalIncidentes", totalIncidentes);
        model.addAttribute("totalObservaciones", totalObservaciones);

        // Cargar incidentes recientes (5 más recientes)
        List<RegistroConducta> incidentesRecientes = registroConductaService.findRecent(5);
        model.addAttribute("incidentesRecientes", incidentesRecientes);

        // --- ¡¡ESTA ES LA PARTE QUE FALTABA!! ---
        // Cargar observaciones recientes (5 más recientes)
        List<Observacion> observacionesRecientes = observacionService.findRecent(5);
        model.addAttribute("observacionesRecientes", observacionesRecientes);
        // --- FIN DE LA PARTE QUE FALTABA ---

        return "director/dashboard"; // Devuelve la vista correcta
    }
}