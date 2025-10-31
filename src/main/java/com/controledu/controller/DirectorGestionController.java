package com.controledu.controller;

import com.controledu.model.*;
import com.controledu.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para todas las operaciones de gestión del Director.
 * Maneja docentes, estudiantes y conductas.
 *
 * Notas generales:
 * - Todas las rutas están bajo el prefijo /director/gestion.
 * - Se valida que exista un Director en sesión antes de renderizar/operar.
 * - Se utilizan RedirectAttributes para mensajes flash (success/error).
 * - El renderizado se hace con vistas tipo "director/*" (Thymeleaf u otro motor).
 */
@Controller
@RequestMapping("/director/gestion")
@RequiredArgsConstructor
public class DirectorGestionController {

    // Servicios de dominio inyectados (lógica de negocio/persistencia)
    private final DocenteService docenteService;
    private final EstudianteService estudianteService;
    private final ConductaService conductaService;
    private final TipoGravedadService tipoGravedadService;

    // =================================================================
    // GESTIÓN DE DOCENTES
    // =================================================================

    /**
     * Muestra la página de gestión de docentes.
     * Carga:
     * - Lista completa de docentes (docentes)
     * - Un objeto vacío Docente para el formulario (docente)
     * Además, adjunta el director autenticado al modelo.
     */
    @GetMapping("/docentes")
    public String gestionDocentes(Model model, HttpSession session) {
        // Control de acceso básico por sesión
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);

        model.addAttribute("docentes", docenteService.findAll());
        model.addAttribute("docente", new Docente());
        return "director/gestion-docentes";
    }

    /**
     * Procesa la creación de un nuevo docente.
     * Recibe el objeto mapeado desde el formulario con @ModelAttribute.
     * En caso de éxito/error, fija un mensaje flash y redirige a la misma página.
     */
    @PostMapping("/docentes/crear")
    public String crearDocente(@ModelAttribute Docente docente, RedirectAttributes redirectAttributes) {
        try {
            docenteService.save(docente);
            redirectAttributes.addFlashAttribute("success", "Docente creado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el docente: " + e.getMessage());
        }
        return "redirect:/director/gestion/docentes";
    }

    /**
     * Procesa la edición de un docente existente.
     * Se asume que el objeto Docente contiene el ID válido.
     */
    @PostMapping("/docentes/editar")
    public String editarDocente(@ModelAttribute Docente docente, RedirectAttributes redirectAttributes) {
        try {
            docenteService.update(docente);
            redirectAttributes.addFlashAttribute("success", "Docente actualizado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el docente: " + e.getMessage());
        }
        return "redirect:/director/gestion/docentes";
    }

    /**
     * Procesa la eliminación de un docente.
     * La operación llega como GET con el {id} en la ruta.
     */
    @GetMapping("/docentes/eliminar/{id}")
    public String eliminarDocente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            docenteService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Docente eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el docente: " + e.getMessage());
        }
        return "redirect:/director/gestion/docentes";
    }

    // =================================================================
    // GESTIÓN DE ESTUDIANTES
    // =================================================================

    /**
     * Muestra la página de gestión de estudiantes.
     * Carga:
     * - Lista completa de estudiantes (estudiantes)
     * - Objeto Estudiante vacío para formulario (estudiante)
     * - Catálogos de grados y secciones para filtros (grados, secciones)
     */
    @GetMapping("/estudiantes")
    public String gestionEstudiantes(Model model, HttpSession session) {
        // Verificación de sesión y adjuntar director al modelo
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);

        model.addAttribute("estudiantes", estudianteService.findAll());
        model.addAttribute("estudiante", new Estudiante());

        // Carga catálogos auxiliares para la vista (filtros/combos)
        model.addAttribute("grados", estudianteService.findAllGradosDistinct());
        model.addAttribute("secciones", estudianteService.findAllSeccionesDistinct());

        return "director/gestion-estudiantes";
    }

    /**
     * Procesa la creación de un nuevo estudiante.
     */
    @PostMapping("/estudiantes/crear")
    public String crearEstudiante(@ModelAttribute Estudiante estudiante, RedirectAttributes redirectAttributes) {
        try {
            estudianteService.save(estudiante);
            redirectAttributes.addFlashAttribute("success", "Estudiante creado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el estudiante: " + e.getMessage());
        }
        return "redirect:/director/gestion/estudiantes";
    }

    /**
     * Procesa la edición de un estudiante existente.
     * Se asume que el objeto Estudiante trae el ID correspondiente.
     */
    @PostMapping("/estudiantes/editar")
    public String editarEstudiante(@ModelAttribute Estudiante estudiante, RedirectAttributes redirectAttributes) {
        try {
            estudianteService.update(estudiante); // Método update en EstudianteService
            redirectAttributes.addFlashAttribute("success", "Estudiante actualizado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el estudiante: " + e.getMessage());
        }
        return "redirect:/director/gestion/estudiantes";
    }

    /**
     * Procesa la eliminación de un estudiante por ID.
     */
    @GetMapping("/estudiantes/eliminar/{id}")
    public String eliminarEstudiante(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            estudianteService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Estudiante eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el estudiante: " + e.getMessage());
        }
        return "redirect:/director/gestion/estudiantes";
    }


    // =================================================================
    // GESTIÓN DE CONDUCTAS (REGLAS)
    // =================================================================

    /**
     * Muestra la página de gestión de conductas (reglas).
     * Carga listado de conductas, catálogo de gravedades y un formulario vacío.
     */
    @GetMapping("/conductas")
    public String gestionConductas(Model model, HttpSession session) {
        // Seguridad por sesión
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);

        model.addAttribute("conductas", conductaService.findAll());
        model.addAttribute("gravedades", tipoGravedadService.findAll());
        model.addAttribute("conducta", new Conducta());
        return "director/gestion-conductas";
    }

    /**
     * Procesa la creación de una nueva conducta.
     * Recibe campos individuales desde el formulario mediante @RequestParam.
     */
    @PostMapping("/conductas/crear")
    public String crearConducta(@RequestParam String nombreConducta,
                                @RequestParam String descripcion,
                                @RequestParam Long idGravedad,
                                RedirectAttributes redirectAttributes) {
        try {
            conductaService.createConducta(nombreConducta, descripcion, idGravedad);
            redirectAttributes.addFlashAttribute("success", "Conducta creada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la conducta: " + e.getMessage());
        }
        return "redirect:/director/gestion/conductas";
    }

    /**
     * Procesa la actualización de una conducta existente.
     * Requiere el id de la conducta y campos a actualizar.
     */
    @PostMapping("/conductas/editar")
    public String editarConducta(@RequestParam Long idConducta,
                                 @RequestParam String nombreConducta,
                                 @RequestParam String descripcion,
                                 @RequestParam Long idGravedad,
                                 RedirectAttributes redirectAttributes) {
        try {
            conductaService.update(idConducta, nombreConducta, descripcion, idGravedad);
            redirectAttributes.addFlashAttribute("success", "Conducta actualizada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la conducta: " + e.getMessage());
        }
        return "redirect:/director/gestion/conductas";
    }

    /**
     * Procesa la eliminación de una conducta por su ID.
     */
    @GetMapping("/conductas/eliminar/{id}")
    public String eliminarConducta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            conductaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Conducta eliminada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la conducta: " + e.getMessage());
        }
        return "redirect:/director/gestion/conductas";
    }

    /**
     * Activa una conducta por su ID (cambio de estado lógico).
     */
    @GetMapping("/conductas/activar/{id}")
    public String activarConducta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            conductaService.activarConducta(id);
            redirectAttributes.addFlashAttribute("success", "Conducta activada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar la conducta: " + e.getMessage());
        }
        return "redirect:/director/gestion/conductas";
    }

    /**
     * Desactiva una conducta por su ID (cambio de estado lógico).
     */
    @GetMapping("/conductas/desactivar/{id}")
    public String desactivarConducta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            conductaService.desactivarConducta(id);
            redirectAttributes.addFlashAttribute("success", "Conducta desactivada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar la conducta: " + e.getMessage());
        }
        return "redirect:/director/gestion/conductas";
    }
}
