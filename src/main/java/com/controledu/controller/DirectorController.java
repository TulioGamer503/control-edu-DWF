package com.controledu.controller;

import com.controledu.model.Director;
import com.controledu.model.Observacion;
import com.controledu.model.RegistroConducta;
import com.controledu.model.TipoGravedad;
import com.controledu.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/director")
@RequiredArgsConstructor
public class DirectorController {

    // --- SERVICIOS INYECTADOS ---
    private final DirectorService directorService;
    private final DocenteService docenteService; // <-- Necesario para totalDocentes
    private final EstudianteService estudianteService;
    private final ConductaService conductaService; // <-- Necesario para topConductas
    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService; // <-- Necesario para totalObservaciones
    private final TipoGravedadService tipoGravedadService;

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        long totalEstudiantes = estudianteService.count();
        long totalDocentes = docenteService.count();
        long totalIncidentes = registroConductaService.count();
        long totalObservaciones = observacionService.count();

        model.addAttribute("director", director);
        model.addAttribute("totalEstudiantes", totalEstudiantes);
        model.addAttribute("totalDocentes", totalDocentes);
        model.addAttribute("totalIncidentes", totalIncidentes);
        model.addAttribute("totalObservaciones", totalObservaciones);

        List<RegistroConducta> incidentesRecientes = registroConductaService.findRecent(5);
        if (incidentesRecientes == null) incidentesRecientes = new ArrayList<>();
        model.addAttribute("incidentesRecientes", incidentesRecientes);

        List<Observacion> observacionesRecientes = observacionService.findRecent(5);
        if (observacionesRecientes == null) observacionesRecientes = new ArrayList<>();
        model.addAttribute("observacionesRecientes", observacionesRecientes);

        return "director/dashboard";
    }

    // --- GESTIÓN DE INCIDENTES (LISTA) ---
    @GetMapping("/incidentes")
    public String mostrarPaginaIncidentes(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("director", director);

        List<TipoGravedad> gravedades = tipoGravedadService.findAll();
        if (gravedades == null) gravedades = new ArrayList<>();
        model.addAttribute("gravedades", gravedades);

        List<String> grados = estudianteService.findAllGradosDistinct();
        if (grados == null) grados = new ArrayList<>();
        model.addAttribute("grados", grados);

        List<RegistroConducta> incidentes = registroConductaService.findAll();

        if (incidentes == null) incidentes = new ArrayList<>();
        model.addAttribute("incidentes", incidentes);

        model.addAttribute("totalIncidentes", registroConductaService.count());
        model.addAttribute("totalNoLeidos", registroConductaService.countByLeido(false));
        model.addAttribute("totalResueltos", registroConductaService.countByEstado("RESUELTO"));
        model.addAttribute("totalActivos", registroConductaService.countByEstado("ACTIVO"));

        return "director/incidentes";
    }


    @GetMapping("/reportes")
    public String mostrarPaginaReportes(HttpSession session, Model model) {
        // 1. Verificación de sesión
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        // 2. Añadir director al modelo
        model.addAttribute("director", director);

        // --- 3. CARGAR TODOS LOS DATOS PARA LOS GRÁFICOS Y TARJETAS ---

        long totalEstudiantes = estudianteService.count();
        long totalIncidentes = registroConductaService.count();
        long totalDocentes = docenteService.count();
        long totalObservaciones = observacionService.count();
        List<String> grados = estudianteService.findAllGradosDistinct();

        // Cálculo seguro para el promedio (esto arregla el error de sintaxis)
        double promedioIncidentes = (totalEstudiantes > 0) ? (totalIncidentes * 1.0 / totalEstudiantes) : 0.0;

        // Añadimos los totales y el promedio al modelo
        model.addAttribute("totalEstudiantes", totalEstudiantes);
        model.addAttribute("totalDocentes", totalDocentes); // <-- Añadido
        model.addAttribute("totalIncidentes", totalIncidentes);
        model.addAttribute("totalObservaciones", totalObservaciones); // <-- Añadido


        model.addAttribute("grados", (grados != null ? grados : new ArrayList<>())); // <-- Añadido

        model.addAttribute("promedioIncidentes", promedioIncidentes); // Para el cálculo


        model.addAttribute("datosGravedad", registroConductaService.countByGravedad());
        model.addAttribute("datosGrado", registroConductaService.countByGrado());
        model.addAttribute("datosMes", registroConductaService.countByMes());
        model.addAttribute("topConductas", conductaService.findConductasMasUtilizadas());

        return "director/reportes";
    }




    @GetMapping("/estudiantes")
    public String mostrarPaginaEstudiantes(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        return "director/estudiantes";
    }

    @GetMapping("/docentes")
    public String mostrarPaginaDocentes(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        return "director/docentes";
    }

    @GetMapping("/conductas")
    public String mostrarPaginaConductas(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        return "director/conductas";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        return "director/perfil";
    }


    // --- MÉTODOS PARA LOS BOTONES DE INCIDENTES ---
    @GetMapping("/incidentes/detalle/{id}")
    public String verDetalleIncidente(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        Optional<RegistroConducta> incidenteOpt = registroConductaService.findById(id);

        if (incidenteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Incidente no encontrado.");
            return "redirect:/director/incidentes";
        }

        model.addAttribute("director", director);
        model.addAttribute("incidente", incidenteOpt.get());

        return "director/incidente-detalle";
    }

    @PostMapping("/incidentes/marcar-leido/{id}")
    public String marcarIncidenteLeido(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        try {
            registroConductaService.marcarComoLeido(id);
            redirectAttributes.addFlashAttribute("successMessage", "Incidente marcado como leído.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al marcar como leído: " + e.getMessage());
        }

        return "redirect:/director/incidentes";
    }

    @PostMapping("/incidentes/resolver/{id}")
    public String resolverIncidente(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        try {
            registroConductaService.cambiarEstado(id, "RESUELTO");
            redirectAttributes.addFlashAttribute("successMessage", "Incidente marcado como resuelto.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al resolver el incidente: " + e.getMessage());
        }

        return "redirect:/director/incidentes";
    }
}