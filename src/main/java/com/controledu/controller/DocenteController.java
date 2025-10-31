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
import jakarta.persistence.EntityNotFoundException; // Para manejo de errores de entidades no encontradas
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // Para recibir los IDs desde el formulario

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controlador MVC para operaciones del rol DOCENTE.
 * Gestiona:
 * - Dashboard con métricas del docente.
 * - Listado de estudiantes.
 * - Registro de faltas (incidentes) y observaciones.
 * - Historial combinado (incidentes + observaciones) ordenado por fecha.
 * - Perfil del docente.
 *
 * Seguridad básica:
 * - Verifica la existencia del objeto "usuario" (Docente) en sesión.
 */
@Controller
@RequestMapping("/docente")
@RequiredArgsConstructor
public class DocenteController {

    // Servicios/repositorios requeridos por el controlador
    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService;
    private final EstudianteRepository estudianteRepository;
    private final ConductaRepository conductaRepository;

    // --- DASHBOARD ---
    /**
     * Muestra el dashboard del docente con métricas y últimos incidentes.
     * - Requiere docente autenticado en sesión.
     * - KPIs: total de incidentes y observaciones del docente.
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            // Si no hay sesión, redirige a login
            return "redirect:/auth/login";
        }
        // Conteos por docente (usando su ID)
        long totalIncidentes = registroConductaService.countByDocenteId(docente.getId());
        long totalObservaciones = observacionService.countByDocenteId(docente.getId());

        // Incidentes asociados al docente (puede considerar limitar a N recientes en el servicio)
        List<RegistroConducta> incidentesRecientes = registroConductaService.findByDocenteId(docente.getId());
        if (incidentesRecientes == null) {
            incidentesRecientes = new ArrayList<>();
        }

        // Datos para la vista
        model.addAttribute("docente", docente);
        model.addAttribute("totalIncidentes", totalIncidentes);
        model.addAttribute("totalObservaciones", totalObservaciones);
        model.addAttribute("incidentesRecientes", incidentesRecientes);
        return "docente/dashboard";
    }

    // --- VER ESTUDIANTES ---
    /**
     * Lista todos los estudiantes (catálogo simple) para que el docente pueda consultarlos.
     * También expone en el modelo los servicios de registro/observación si la vista los utiliza.
     */
    @GetMapping("/estudiantes")
    public String verEstudiantes(HttpSession session, Model model) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        // Obtiene todos los estudiantes (podría paginarse si la lista crece)
        List<Estudiante> estudiantes = estudianteRepository.findAll();

        model.addAttribute("docente", docente);
        model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
        // Exponer servicios a la vista (si se invocan métodos desde la plantilla)
        model.addAttribute("registroConductaService", registroConductaService);
        model.addAttribute("observacionService", observacionService);
        return "docente/estudiantes";
    }

    // --- REGISTRAR FALTA (GET - Mostrar Formulario) ---
    // **** CORREGIDO ****
    /**
     * Muestra el formulario para registrar una falta (incidente) a un estudiante.
     * - Carga listas de estudiantes y conductas para combos.
     * - Acepta opcionalmente un estudiante preseleccionado (estudianteIdSeleccionado).
     */
    @GetMapping("/registrar-falta")
    public String registrarFaltaForm(HttpSession session, Model model,
                                     @RequestParam(value = "estudianteId", required = false) Long estudianteIdSeleccionado) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        // Catálogos para la vista
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        List<Conducta> conductas = conductaRepository.findAll();

        model.addAttribute("docente", docente);
        model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
        model.addAttribute("conductas", conductas != null ? conductas : new ArrayList<>());

        // Objeto vacío para binding de campos simples del formulario (p.ej. accionesTomadas)
        model.addAttribute("registroConducta", new RegistroConducta());
        // Mantener el estudiante seleccionado (si llegó por query param)
        model.addAttribute("estudianteIdSeleccionado", estudianteIdSeleccionado);

        return "docente/registrar-falta";
    }

    // --- REGISTRAR FALTA (POST - Guardar Datos) ---
    // **** CORREGIDO ****
    /**
     * Guarda una falta (RegistroConducta) asociando Estudiante y Conducta por sus IDs.
     * - Valida sesión del docente.
     * - Maneja ausencia de entidades con EntityNotFoundException y re-render del formulario.
     */
    @PostMapping("/registrar-falta")
    public String guardarFalta(
            @ModelAttribute RegistroConducta registroConducta, // Recibe campos simples (ej. accionesTomadas)
            @RequestParam("estudianteId") Long estudianteId, // ID del estudiante seleccionado
            @RequestParam("conductaId") Long conductaId,   // ID de la conducta seleccionada
            HttpSession session,
            Model model) { // Model para devolver errores/datos en caso de fallo

        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }

        try {
            // 1. Buscar Estudiante (o lanzar excepción si no existe)
            Estudiante estudiante = estudianteRepository.findById(estudianteId)
                    .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));

            // 2. Buscar Conducta (o lanzar excepción si no existe)
            Conducta conducta = conductaRepository.findById(conductaId)
                    .orElseThrow(() -> new EntityNotFoundException("Conducta no encontrada"));

            // 3. Poblar el objeto de dominio con relaciones y metadatos
            registroConducta.setEstudiante(estudiante);
            registroConducta.setConducta(conducta);
            registroConducta.setDocente(docente);
            registroConducta.setFechaRegistro(LocalDate.now()); // Fecha actual; usar LocalDateTime si se requiere hora

            // 4. Persistir el registro
            registroConductaService.guardar(registroConducta);

            // Redirigir a historial tras el éxito
            return "redirect:/docente/historial";

        } catch (EntityNotFoundException e) {
            // 5. Si faltan entidades, mostrar mensaje y volver al formulario conservando datos
            model.addAttribute("errorMessage", e.getMessage());

            // Recargar catálogos necesarios para la vista
            List<Estudiante> estudiantes = estudianteRepository.findAll();
            List<Conducta> conductas = conductaRepository.findAll();
            model.addAttribute("docente", docente);
            model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
            model.addAttribute("conductas", conductas != null ? conductas : new ArrayList<>());
            model.addAttribute("registroConducta", registroConducta); // Conservar campos ya ingresados
            model.addAttribute("estudianteIdSeleccionado", estudianteId); // Mantener selección previa
            return "docente/registrar-falta";
        }
    }

    // --- REGISTRAR OBSERVACIÓN (GET - Mostrar Formulario) ---
    // **** CORREGIDO ****
    /**
     * Muestra el formulario de registro de observación para un estudiante.
     * - Carga catálogo de estudiantes.
     * - Permite llegar con un estudiante preseleccionado.
     */
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
        model.addAttribute("observacion", new Observacion()); // Objeto para binding del formulario
        model.addAttribute("estudianteIdSeleccionado", estudianteIdSeleccionado); // Mantener selección si viene por query
        return "docente/registrar-observacion";
    }

    // --- REGISTRAR OBSERVACIÓN (POST - Guardar Datos) ---
    // (Este ya estaba corregido)
    /**
     * Persiste una observación para un estudiante.
     * - Resuelve el Estudiante por ID o muestra error en el mismo formulario.
     * - Asocia el Docente de sesión y la fecha actual.
     */
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
            // Buscar estudiante o devolver mensaje de error
            Estudiante estudiante = estudianteRepository.findById(estudianteId)
                    .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + estudianteId));
            observacion.setEstudiante(estudiante);
        } catch (EntityNotFoundException e) {
            // Devolver al formulario con mensaje y datos previos
            model.addAttribute("errorMessage", e.getMessage());
            List<Estudiante> estudiantes = estudianteRepository.findAll();
            model.addAttribute("docente", docente);
            model.addAttribute("estudiantes", estudiantes != null ? estudiantes : new ArrayList<>());
            model.addAttribute("observacion", observacion);
            model.addAttribute("estudianteIdSeleccionado", estudianteId); // Mantener selección previa
            return "docente/registrar-observacion";
        }

        // Completar datos restantes y guardar
        observacion.setDocente(docente);
        observacion.setFecha(LocalDate.now());
        observacionService.guardar(observacion);

        return "redirect:/docente/historial";
    }

    // --- HISTORIAL ---
    /**
     * Combina en una sola lista los incidentes y observaciones del docente,
     * ordenados por fecha descendente, para una vista unificada del historial.
     */
    @GetMapping("/historial")
    public String verHistorial(HttpSession session, Model model) {
        Docente docente = (Docente) session.getAttribute("usuario");
        if (docente == null) {
            return "redirect:/auth/login";
        }
        // Cargar items propios del docente
        List<RegistroConducta> incidentes = registroConductaService.findByDocenteId(docente.getId());
        List<Observacion> observaciones = observacionService.findByDocenteId(docente.getId());
        if (incidentes == null) { incidentes = new ArrayList<>(); }
        if (observaciones == null) { observaciones = new ArrayList<>(); }

        // Lista heterogénea para mezclar ambos tipos
        List<Object> historialItems = new ArrayList<>();
        historialItems.addAll(incidentes);
        historialItems.addAll(observaciones);

        // Ordenar por fecha (desc). Se contemplan posibles nulls en fechas.
        historialItems.sort(Comparator.comparing(item -> {
            if (item instanceof RegistroConducta rc && rc.getFechaRegistro() != null) {
                return rc.getFechaRegistro(); // Fecha del incidente
            } else if (item instanceof Observacion obs && obs.getFecha() != null) {
                return obs.getFecha(); // Fecha de la observación
            }
            return LocalDate.MIN; // Valor por defecto para elementos sin fecha
        }, Comparator.nullsLast(LocalDate::compareTo).reversed()));

        model.addAttribute("docente", docente);
        model.addAttribute("historialItems", historialItems);
        return "docente/historial";
    }

    // --- PERFIL ---
    /**
     * Muestra la vista del perfil del docente autenticado.
     */
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
