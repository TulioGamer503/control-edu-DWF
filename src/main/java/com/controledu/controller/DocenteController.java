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

// Importaciones necesarias
import jakarta.persistence.EntityNotFoundException; // Para manejo de errores
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // Para recibir los IDs

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/docente")
@RequiredArgsConstructor
public class DocenteController {

    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService;
    private final EstudianteRepository estudianteRepository;
    private final ConductaRepository conductaRepository;

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
    // **** CORREGIDO ****
    @GetMapping("/registrar-falta")
    public String registrarFaltaForm(HttpSession session, Model model,
                                     @RequestParam(value = "estudianteId", required = false) Long estudianteIdSeleccionado) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        List<Conducta> conductas = conductaRepository.findAll();
        model.addAttribute("docente", docente);
        model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
        model.addAttribute("conductas", conductas != null ? conductas : new ArrayList<>());

        // Pasa un objeto vacío para el binding de campos simples (como accionesTomadas)
        model.addAttribute("registroConducta", new RegistroConducta());
        // Pasa el ID seleccionado para que el HTML lo use
        model.addAttribute("estudianteIdSeleccionado", estudianteIdSeleccionado);

        return "docente/registrar-falta";
    }

    // --- REGISTRAR FALTA (POST - Guardar Datos) ---
    // **** CORREGIDO ****
    @PostMapping("/registrar-falta")
    public String guardarFalta(
            @ModelAttribute RegistroConducta registroConducta, // Recibe campos simples (accionesTomadas)
            @RequestParam("estudianteId") Long estudianteId, // Recibe ID de estudiante
            @RequestParam("conductaId") Long conductaId,   // Recibe ID de conducta
            HttpSession session,
            Model model) { // Añadido Model para manejo de errores

        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }

        try {
            // 1. Buscar Estudiante
            Estudiante estudiante = estudianteRepository.findById(estudianteId)
                    .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));

            // 2. Buscar Conducta
            Conducta conducta = conductaRepository.findById(conductaId)
                    .orElseThrow(() -> new EntityNotFoundException("Conducta no encontrada"));

            // 3. Poblar el objeto
            registroConducta.setEstudiante(estudiante);
            registroConducta.setConducta(conducta);
            registroConducta.setDocente(docente);
            registroConducta.setFechaRegistro(LocalDate.now()); // O LocalDateTime.now()

            // 4. Guardar
            registroConductaService.guardar(registroConducta);

            return "redirect:/docente/historial"; // Éxito

        } catch (EntityNotFoundException e) {
            // 5. Manejar error si no se encuentra Estudiante o Conducta
            model.addAttribute("errorMessage", e.getMessage());
            // Recargar datos para el formulario
            List<Estudiante> estudiantes = estudianteRepository.findAll();
            List<Conducta> conductas = conductaRepository.findAll();
            model.addAttribute("docente", docente);
            model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
            model.addAttribute("conductas", conductas != null ? conductas : new ArrayList<>());
            model.addAttribute("registroConducta", registroConducta); // Devolver datos ingresados (ej. accionesTomadas)
            model.addAttribute("estudianteIdSeleccionado", estudianteId); // Mantener selección
            return "docente/registrar-falta"; // Volver al formulario
        }
    }

    // --- REGISTRAR OBSERVACIÓN (GET - Mostrar Formulario) ---
    // **** CORREGIDO ****
    @GetMapping("/registrar-observacion")
    public String registrarObservacionForm(HttpSession session, Model model,
                                           @RequestParam(value = "estudianteId", required = false) Long estudianteIdSeleccionado) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        model.addAttribute("docente", docente);
        model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
        model.addAttribute("observacion", new Observacion());

        // Pasa el ID seleccionado para que el HTML lo use
        model.addAttribute("estudianteIdSeleccionado", estudianteIdSeleccionado);

        return "docente/registrar-observacion";
    }

    // --- REGISTRAR OBSERVACIÓN (POST - Guardar Datos) ---
    // (Este ya estaba corregido)
    @PostMapping("/registrar-observacion")
    public String guardarObservacion(
            @ModelAttribute Observacion observacion,
            @RequestParam("estudianteId") Long estudianteId,
            HttpSession session,
            Model model
    ) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }

        try {
            Estudiante estudiante = estudianteRepository.findById(estudianteId)
                    .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + estudianteId));
            observacion.setEstudiante(estudiante);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            List<Estudiante> estudiantes = estudianteRepository.findAll();
            model.addAttribute("docente", docente);
            model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
            model.addAttribute("observacion", observacion);
            model.addAttribute("estudianteIdSeleccionado", estudianteId); // Mantener selección
            return "docente/registrar-observacion";
        }

        observacion.setDocente(docente);
        observacion.setFecha(LocalDate.now());
        observacionService.guardar(observacion);

        return "redirect:/docente/historial";
    }

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

        List<Object> historialItems = new ArrayList<>();
        historialItems.addAll(incidentes);
        historialItems.addAll(observaciones);

        historialItems.sort(Comparator.comparing(item -> {
            if (item instanceof RegistroConducta rc && rc.getFechaRegistro() != null) {
                return rc.getFechaRegistro();
            } else if (item instanceof Observacion obs && obs.getFecha() != null) {
                return obs.getFecha();
            }
            return LocalDate.MIN;
        }, Comparator.nullsLast(LocalDate::compareTo).reversed()));

        model.addAttribute("docente", docente);
        model.addAttribute("historialItems", historialItems);
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
        return "docente/perfil";
    }
}