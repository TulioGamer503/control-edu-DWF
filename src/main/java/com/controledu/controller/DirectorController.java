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

/**
 * Controlador MVC para las vistas y acciones del rol DIRECTOR.
 * Centraliza dashboard, listados (estudiantes, docentes, conductas, observaciones),
 * y acciones sobre incidentes/observaciones (marcar leído, resolver, eliminar, ver detalle).
 *
 * Notas:
 * - Usa HttpSession para recuperar el usuario autenticado y verificar acceso.
 * - Carga datos en el Model para renderizado de vistas Thymeleaf (o el motor que uses).
 * - Maneja mensajes transitorios con RedirectAttributes (success/error/warning).
 */
@Controller
@RequestMapping("/director")
@RequiredArgsConstructor
public class DirectorController {

    // --- SERVICIOS INYECTADOS ---
    // Servicios de dominio que encapsulan lógica de negocio/consulta de datos.
    private final DirectorService directorService;
    private final DocenteService docenteService;
    private final EstudianteService estudianteService;
    private final ConductaService conductaService;
    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService;
    private final TipoGravedadService tipoGravedadService;

    // --- DASHBOARD ---
    /**
     * Renderiza el dashboard del director con métricas y listados recientes.
     * Verifica sesión activa; si no hay usuario en sesión, redirige a login.
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Recupera el usuario (Director) de la sesión.
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            // Si no hay sesión válida, forzamos login.
            return "redirect:/auth/login";
        }

        // Métricas agregadas para tarjetas/resúmenes del dashboard.
        long totalEstudiantes = estudianteService.count();
        long totalDocentes = docenteService.count();
        long totalIncidentes = registroConductaService.count();
        long totalObservaciones = observacionService.count();

        // Atributos de la vista
        model.addAttribute("director", director);
        model.addAttribute("totalEstudiantes", totalEstudiantes);
        model.addAttribute("totalDocentes", totalDocentes);
        model.addAttribute("totalIncidentes", totalIncidentes);
        model.addAttribute("totalObservaciones", totalObservaciones);

        // Últimos incidentes registrados (limite 5). Se protege contra null.
        List<RegistroConducta> incidentesRecientes = registroConductaService.findRecent(5);
        if (incidentesRecientes == null) incidentesRecientes = new ArrayList<>();
        model.addAttribute("incidentesRecientes", incidentesRecientes);

        // Últimas observaciones (limite 5). Igualmente se protege contra null.
        List<Observacion> observacionesRecientes = observacionService.findRecent(5);
        if (observacionesRecientes == null) observacionesRecientes = new ArrayList<>();
        model.addAttribute("observacionesRecientes", observacionesRecientes);

        // Devuelve la plantilla del dashboard del director.
        return "director/dashboard";
    }

    // --- GESTIÓN DE INCIDENTES (LISTA) ---
    /**
     * Página principal de incidentes para el director con filtros y métricas.
     */
    @GetMapping("/incidentes")
    public String mostrarPaginaIncidentes(HttpSession session, Model model) {
        // Control de acceso: requiere director autenticado en sesión.
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("director", director);

        // Catálogo de tipos de gravedad para filtros/etiquetas.
        List<TipoGravedad> gravedades = tipoGravedadService.findAll();
        if (gravedades == null) gravedades = new ArrayList<>();
        model.addAttribute("gravedades", gravedades);

        // Catálogo de grados distintos para filtros.
        List<String> grados = estudianteService.findAllGradosDistinct();
        if (grados == null) grados = new ArrayList<>();
        model.addAttribute("grados", grados);

        // Listado completo de incidentes (podrías paginar en el servicio si crece).
        List<RegistroConducta> incidentes = registroConductaService.findAll();
        if (incidentes == null) incidentes = new ArrayList<>();
        model.addAttribute("incidentes", incidentes);

        // Métricas de estado para KPIs en la vista.
        model.addAttribute("totalIncidentes", registroConductaService.count());
        model.addAttribute("totalNoLeidos", registroConductaService.countByLeido(false));
        model.addAttribute("totalResueltos", registroConductaService.countByEstado("RESUELTO"));
        model.addAttribute("totalActivos", registroConductaService.countByEstado("ACTIVO"));

        return "director/incidentes";
    }

    // --- REPORTES (ELIMINADO) ---
    /*
    @GetMapping("/reportes")
    public String mostrarPaginaReportes(HttpSession session, Model model) {
        // ... (código eliminado) ...
        return "director/reportes";
    }
    */

    // --- OTRAS PÁGINAS DEL DIRECTOR ---
    /**
     * Listado de estudiantes para el director.
     */
    @GetMapping("/estudiantes")
    public String mostrarPaginaEstudiantes(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        model.addAttribute("estudiantes", estudianteService.findAll()); // Carga la lista de estudiantes
        return "director/estudiantes";
    }

    /**
     * Listado de docentes para el director.
     */
    @GetMapping("/docentes")
    public String mostrarPaginaDocentes(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        model.addAttribute("docentes", docenteService.findAll()); // Carga la lista de docentes
        return "director/docentes";
    }

    /**
     * Catálogo/listado de conductas para el director.
     */
    @GetMapping("/conductas")
    public String mostrarPaginaConductas(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        model.addAttribute("conductas", conductaService.findAll()); // Carga la lista de conductas
        return "director/conductas";
    }

    /**
     * Listado de observaciones registradas.
     */
    @GetMapping("/observaciones")
    public String mostrarPaginaObservaciones(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        // Se protege de posibles null para evitar NPE en la vista.
        List<Observacion> observaciones = observacionService.findAll();
        if (observaciones == null) {
            observaciones = new ArrayList<>();
        }

        model.addAttribute("director", director);
        model.addAttribute("observaciones", observaciones);

        return "director/observaciones";
    }

    /**
     * Muestra el perfil del director autenticado.
     */
    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        return "director/perfil";
    }

    // --- MÉTODOS PARA ACCIONES DE INCIDENTES ---

    /**
     * Vista de detalle para un incidente específico.
     * - Recupera por id usando el servicio y maneja caso no encontrado con flash message.
     * - Marca como leído automáticamente si aún no lo estaba (manejo en try/catch).
     */
    @GetMapping("/incidentes/detalle/{id}")
    public String verDetalleIncidente(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        // Uso de Optional para manejar inexistencia del registro.
        Optional<RegistroConducta> incidenteOpt = registroConductaService.findById(id);

        if (incidenteOpt.isEmpty()) {
            // Mensaje de error y retorno a la lista si no existe el incidente.
            redirectAttributes.addFlashAttribute("errorMessage", "Incidente no encontrado.");
            return "redirect:/director/incidentes";
        }
        RegistroConducta incidente = incidenteOpt.get();
        model.addAttribute("director", director);
        model.addAttribute("incidente", incidente);

        // Si no estaba leído, se intenta marcar automáticamente (controlando excepciones).
        if (!incidente.getLeido()) {
            try {
                registroConductaService.marcarComoLeido(id);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("warningMessage", "No se pudo marcar como leído automáticamente: " + e.getMessage());
            }
        }
        return "director/incidente-detalle";
    }

    /**
     * Acción POST para marcar un incidente como leído.
     * Usa flash messages para retroalimentar el resultado de la operación.
     */
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

    /**
     * Acción POST para cambiar el estado de un incidente a "RESUELTO".
     */
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

    // --- MÉTODOS PARA ACCIONES DE OBSERVACIONES ---

    /**
     * Vista de detalle para una observación específica.
     * Similar al detalle de incidente: valida existencia, carga en modelo,
     * e intenta marcar como leída si aún no lo estaba.
     */
    @GetMapping("/observaciones/detalle/{id}")
    public String verDetalleObservacion(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        Optional<Observacion> observacionOpt = observacionService.findById(id);

        if (observacionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Observación no encontrada.");
            return "redirect:/director/observaciones";
        }
        Observacion observacion = observacionOpt.get();
        model.addAttribute("director", director);
        model.addAttribute("observacion", observacion);

        // Intento de marcado automático como leída.
        if (!observacion.getLeido()) {
            try {
                observacionService.marcarComoLeida(id);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("warningMessage", "No se pudo marcar como leída automáticamente: " + e.getMessage());
            }
        }

        return "director/observacion-detalle";
    }

    /**
     * Acción POST para marcar una observación como leída.
     */
    @PostMapping("/observaciones/marcar-leida/{id}")
    public String marcarObservacionLeida(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        try {
            observacionService.marcarComoLeida(id);
            redirectAttributes.addFlashAttribute("successMessage", "Observación marcada como leída.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al marcar como leída: " + e.getMessage());
        }

        return "redirect:/director/observaciones";
    }

    /**
     * Acción POST para eliminar una observación por su id.
     */
    @PostMapping("/observaciones/eliminar/{id}")
    public String eliminarObservacion(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }

        try {
            observacionService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Observación eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la observación: " + e.getMessage());
        }

        return "redirect:/director/observaciones";
    }
}
