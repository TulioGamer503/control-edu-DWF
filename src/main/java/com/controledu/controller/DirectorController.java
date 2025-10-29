package com.controledu.controller;

import com.controledu.model.Director;
import com.controledu.model.Estudiante;
import com.controledu.model.Conducta;
import com.controledu.model.Observacion;
import com.controledu.model.RegistroConducta;
import com.controledu.model.TipoGravedad;
// Importa todos los servicios necesarios
import com.controledu.service.*;

import jakarta.persistence.EntityNotFoundException; // Para manejo de errores
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate; // O LocalDateTime si usas esa clase
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/director")
@RequiredArgsConstructor
public class DirectorController {

    // --- SERVICIOS INYECTADOS ---
    // Asegúrate de que todos estos servicios existen y están anotados con @Service
    private final DirectorService directorService;
    private final DocenteService docenteService;
    private final EstudianteService estudianteService;
    private final ConductaService conductaService;
    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService;
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

        // Asegúrate que los métodos findRecent existen en los servicios
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

        // Asegúrate que estos métodos count existen en RegistroConductaService
        model.addAttribute("totalIncidentes", registroConductaService.count());
        model.addAttribute("totalNoLeidos", registroConductaService.countByLeido(false));
        model.addAttribute("totalResueltos", registroConductaService.countByEstado("RESUELTO"));
        model.addAttribute("totalActivos", registroConductaService.countByEstado("ACTIVO"));

        return "director/incidentes";
    }

    // --- REPORTES ---
    @GetMapping("/reportes")
    public String mostrarPaginaReportes(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);

        // Asegúrate que los servicios y sus métodos count/find existen
        long totalEstudiantes = estudianteService.count();
        long totalIncidentes = registroConductaService.count();
        long totalDocentes = docenteService.count();
        long totalObservaciones = observacionService.count();
        List<String> grados = estudianteService.findAllGradosDistinct();

        // Cálculo seguro para el promedio (movido aquí desde el HTML)
        double promedioIncidentes = (totalEstudiantes > 0) ? ((double) totalIncidentes / totalEstudiantes) : 0.0;

        // Añadimos los totales y el promedio al modelo
        model.addAttribute("totalEstudiantes", totalEstudiantes);
        model.addAttribute("totalDocentes", totalDocentes);
        model.addAttribute("totalIncidentes", totalIncidentes);
        model.addAttribute("totalObservaciones", totalObservaciones);
        model.addAttribute("promedioIncidentes", promedioIncidentes); // Variable corregida para el HTML

        model.addAttribute("grados", (grados != null ? grados : new ArrayList<>()));

        // Asegúrate que estos métodos existen en RegistroConductaService y ConductaService
        model.addAttribute("datosGravedad", registroConductaService.countByGravedad());
        model.addAttribute("datosGrado", registroConductaService.countByGrado());
        model.addAttribute("datosMes", registroConductaService.countByMes());
        model.addAttribute("topConductas", conductaService.findConductasMasUtilizadas());

        return "director/reportes";
    }

    // --- OTRAS PÁGINAS DEL DIRECTOR ---
    @GetMapping("/estudiantes")
    public String mostrarPaginaEstudiantes(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        model.addAttribute("estudiantes", estudianteService.findAll()); // Carga la lista de estudiantes
        return "director/estudiantes"; // Asegúrate que existe 'director/estudiantes.html'
    }

    @GetMapping("/docentes")
    public String mostrarPaginaDocentes(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        model.addAttribute("docentes", docenteService.findAll()); // Carga la lista de docentes
        return "director/docentes"; // Asegúrate que existe 'director/docentes.html'
    }

    @GetMapping("/conductas")
    public String mostrarPaginaConductas(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        model.addAttribute("conductas", conductaService.findAll()); // Carga la lista de conductas
        return "director/conductas"; // Asegúrate que existe 'director/conductas.html'
    }

    @GetMapping("/observaciones")
    public String mostrarPaginaObservaciones(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        // Carga la lista de todas las observaciones
        List<Observacion> observaciones = observacionService.findAll();
        if (observaciones == null) {
            observaciones = new ArrayList<>();
        }

        model.addAttribute("director", director);
        model.addAttribute("observaciones", observaciones); // Añade la lista al modelo

        // Asegúrate de crear el archivo 'director/observaciones.html'
        return "director/observaciones";
    }


    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        return "director/perfil"; // Asegúrate que existe 'director/perfil.html'
    }


    // --- MÉTODOS PARA ACCIONES DE INCIDENTES ---
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
        RegistroConducta incidente = incidenteOpt.get();
        model.addAttribute("director", director);
        model.addAttribute("incidente", incidente);

        // Marcar como leído al ver el detalle (si aún no lo está)
        if (!incidente.getLeido()) { // Usando getLeido()
            try {
                registroConductaService.marcarComoLeido(id);
            } catch (Exception e) {
                // Manejar error si falla el marcado automático
                redirectAttributes.addFlashAttribute("warningMessage", "No se pudo marcar como leído automáticamente: " + e.getMessage());
            }
        }
        return "director/incidente-detalle"; // Asegúrate que existe 'director/incidente-detalle.html'
    }

    @PostMapping("/incidentes/marcar-leido/{id}")
    public String marcarIncidenteLeido(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        try {
            // Asegúrate que el método marcarComoLeido existe y funciona
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
            // Asegúrate que el método cambiarEstado existe y funciona
            registroConductaService.cambiarEstado(id, "RESUELTO");
            redirectAttributes.addFlashAttribute("successMessage", "Incidente marcado como resuelto.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al resolver el incidente: " + e.getMessage());
        }

        return "redirect:/director/incidentes";
    }

    // --- MÉTODOS PARA ACCIONES DE OBSERVACIONES ---

    @GetMapping("/observaciones/detalle/{id}")
    public String verDetalleObservacion(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        Optional<Observacion> observacionOpt = observacionService.findById(id);

        if (observacionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Observación no encontrada.");
            // Redirige a la lista de observaciones si no se encuentra
            return "redirect:/director/observaciones";
        }
        Observacion observacion = observacionOpt.get();
        model.addAttribute("director", director);
        model.addAttribute("observacion", observacion);

        // Opcional: Marcar como leída al verla
        // Asumiendo que Observacion tiene getLeido()
        if (!observacion.getLeido()) {
            try {
                observacionService.marcarComoLeida(id);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("warningMessage", "No se pudo marcar como leída automáticamente: " + e.getMessage());
            }
        }

        // Necesitarás crear este archivo HTML: 'director/observacion-detalle.html'
        return "director/observacion-detalle";
    }

    @PostMapping("/observaciones/marcar-leida/{id}")
    public String marcarObservacionLeida(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        try {
            // Asegúrate que el método marcarComoLeida existe en ObservacionService
            observacionService.marcarComoLeida(id);
            redirectAttributes.addFlashAttribute("successMessage", "Observación marcada como leída.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al marcar como leída: " + e.getMessage());
        }

        // Redirige de vuelta a la lista de observaciones
        return "redirect:/director/observaciones";
    }

    @PostMapping("/observaciones/eliminar/{id}")
    public String eliminarObservacion(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        try {
            // Asegúrate que el método deleteById existe en ObservacionService
            observacionService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Observación eliminada correctamente.");
        } catch (Exception e) {
            // Captura errores, por ejemplo, si la observación no existe
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la observación: " + e.getMessage());
        }

        // Redirige de vuelta a la lista de observaciones
        return "redirect:/director/observaciones";
    }
}