package com.controledu.controller;

import com.controledu.model.Docente;
import com.controledu.model.Estudiante;
import com.controledu.model.Conducta;
import com.controledu.model.RegistroConducta;
import com.controledu.model.Observacion;
import com.controledu.repository.EstudianteRepository;
import com.controledu.repository.ConductaRepository;
import com.controledu.service.RegistroConductaService;
import com.controledu.service.ObservacionService;

// Import necessary annotations and classes
import jakarta.persistence.EntityNotFoundException; // For error handling
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // For receiving estudianteId

import java.time.LocalDate; // O LocalDateTime si usas esa clase
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/docente")
@RequiredArgsConstructor
public class DocenteController {

    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService;
    private final EstudianteRepository estudianteRepository; // Asegúrate de que existe
    private final ConductaRepository conductaRepository; // Asegúrate de que existe

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        long totalIncidentes = registroConductaService.countByDocenteId(docente.getId());
        long totalObservaciones = observacionService.countByDocenteId(docente.getId());
        List<RegistroConducta> incidentesRecientes = registroConductaService.findByDocenteId(docente.getId());
        if (incidentesRecientes == null) {
            incidentesRecientes = new ArrayList<>();
        }
        model.addAttribute("docente", docente);
        model.addAttribute("totalIncidentes", totalIncidentes);
        model.addAttribute("totalObservaciones", totalObservaciones);
        model.addAttribute("incidentesRecientes", incidentesRecientes);
        return "docente/dashboard";
    }

    // --- VER ESTUDIANTES ---
    @GetMapping("/estudiantes")
    public String verEstudiantes(HttpSession session, Model model) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        model.addAttribute("docente", docente);
        model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
        model.addAttribute("registroConductaService", registroConductaService);
        model.addAttribute("observacionService", observacionService);
        return "docente/estudiantes";
    }

    // --- REGISTRAR FALTA (GET - Mostrar Formulario) ---
    @GetMapping("/registrar-falta")
    public String registrarFaltaForm(HttpSession session, Model model) { // Renombrado para claridad
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        List<Conducta> conductas = conductaRepository.findAll();
        model.addAttribute("docente", docente);
        model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
        model.addAttribute("conductas", conductas != null ? conductas : new ArrayList<>());
        model.addAttribute("registroConducta", new RegistroConducta());
        return "docente/registrar-falta";
    }

    // --- REGISTRAR FALTA (POST - Guardar Datos) ---
    @PostMapping("/registrar-falta")
    public String guardarFalta(@ModelAttribute RegistroConducta registroConducta, HttpSession session) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        registroConducta.setDocente(docente);
        registroConducta.setFechaRegistro(LocalDate.now()); // O LocalDateTime.now()
        registroConductaService.guardar(registroConducta); // Assumes service has 'guardar'
        return "redirect:/docente/historial"; // Redirige al historial después de guardar
    }

    // --- REGISTRAR OBSERVACIÓN (GET - Mostrar Formulario) ---
    @GetMapping("/registrar-observacion")
    public String registrarObservacionForm(HttpSession session, Model model) { // Renombrado para claridad
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        model.addAttribute("docente", docente);
        model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
        model.addAttribute("observacion", new Observacion());
        return "docente/registrar-observacion";
    }

    // --- REGISTRAR OBSERVACIÓN (POST - Guardar Datos) ---
    // **** CORRECCIÓN APLICADA AQUÍ ****
    @PostMapping("/registrar-observacion")
    public String guardarObservacion(
            @ModelAttribute Observacion observacion, // Datos del formulario (descripción, tipo, etc.)
            @RequestParam("estudianteId") Long estudianteId, // <-- Recibe el ID del estudiante
            HttpSession session,
            Model model // <-- Añadido para manejo de errores
    ) {

        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }

        // --- Buscar y asignar el Estudiante ---
        try {
            Estudiante estudiante = estudianteRepository.findById(estudianteId)
                    .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + estudianteId));
            observacion.setEstudiante(estudiante); // <-- Asignar el estudiante encontrado
        } catch (EntityNotFoundException e) {
            // Manejo de error: Volver al formulario si el estudiante no existe
            model.addAttribute("errorMessage", e.getMessage());
            List<Estudiante> estudiantes = estudianteRepository.findAll(); // Recargar lista para el select
            model.addAttribute("docente", docente);
            model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
            model.addAttribute("observacion", observacion); // Mantener datos ingresados
            return "docente/registrar-observacion"; // Mostrar formulario de nuevo
        }
        // --- Fin de buscar/asignar estudiante ---

        observacion.setDocente(docente);
        observacion.setFecha(LocalDate.now()); // O LocalDateTime.now()

        // Guardar la observación (ahora con el estudiante asignado)
        observacionService.guardar(observacion); // Assumes service has 'guardar'

        return "redirect:/docente/historial"; // Redirige al historial después de guardar
    }
    // **** FIN DE LA CORRECCIÓN ****

    // --- HISTORIAL ---
    @GetMapping("/historial")
    public String verHistorial(HttpSession session, Model model) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        List<RegistroConducta> incidentes = registroConductaService.findByDocenteId(docente.getId());
        List<Observacion> observaciones = observacionService.findByDocenteId(docente.getId());
        if (incidentes == null) { incidentes = new ArrayList<>(); }
        if (observaciones == null) { observaciones = new ArrayList<>(); }

        // Combinar y ordenar para la línea de tiempo
        List<Object> historialItems = new ArrayList<>();
        historialItems.addAll(incidentes);
        historialItems.addAll(observaciones);

        historialItems.sort(Comparator.comparing(item -> {
            if (item instanceof RegistroConducta rc && rc.getFechaRegistro() != null) {
                return rc.getFechaRegistro();
            } else if (item instanceof Observacion obs && obs.getFecha() != null) {
                return obs.getFecha();
            }
            return LocalDate.MIN; // Manejo de nulos o tipos inesperados
        }, Comparator.nullsLast(LocalDate::compareTo).reversed())); // reversed() para más nuevos primero

        model.addAttribute("docente", docente);
        model.addAttribute("historialItems", historialItems); // Pasar la lista combinada
        return "docente/historial";
    }

    // --- PERFIL ---
    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("docente", docente);
        // Asegúrate de tener la plantilla 'docente/perfil.html'
        return "docente/perfil";
    }
}