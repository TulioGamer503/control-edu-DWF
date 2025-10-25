package com.controledu.controller;

import com.controledu.model.Estudiante;
// 1. IMPORTA LA LISTA Y TU MODELO DE CONDUCTA
import com.controledu.model.RegistroConducta;
import com.controledu.service.RegistroConductaService;
import com.controledu.service.ObservacionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList; // 2. IMPORTA ARRAYLIST
import java.util.List; // 2. IMPORTA LIST

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

        // --- 3. ESTA ES LA LÓGICA QUE FALTABA ---
        // (Asegúrate de que el método se llame así en tu servicio)
        List<RegistroConducta> faltasRecientes = registroConductaService.findByEstudianteId(estudiante.getId());

        // Para evitar el error de 'null.empty', asegúrate de pasar una lista vacía
        if (faltasRecientes == null) {
            faltasRecientes = new ArrayList<>();
        }

        model.addAttribute("estudiante", estudiante);
        model.addAttribute("totalFaltas", totalFaltas);
        model.addAttribute("totalObservaciones", totalObservaciones);

        // --- 4. AQUÍ SE AGREGA LA VARIABLE AL MODELO ---
        model.addAttribute("faltasRecientes", faltasRecientes);

        return "estudiante/dashboard";
    }
}