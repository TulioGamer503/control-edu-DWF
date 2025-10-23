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
 */
@Controller
@RequestMapping("/director/gestion")
@RequiredArgsConstructor
public class DirectorGestionController {

    private final DocenteService docenteService;
    private final EstudianteService estudianteService;
    private final ConductaService conductaService;
    private final TipoGravedadService tipoGravedadService;

    // =================================================================
    // GESTIÓN DE DOCENTES
    // =================================================================

    /**
     * Muestra la página de gestión de docentes.
     */
    @GetMapping("/docentes")
    public String gestionDocentes(Model model, HttpSession session) {
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
     */
    @GetMapping("/estudiantes")
    public String gestionEstudiantes(Model model, HttpSession session) {
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);

        model.addAttribute("estudiantes", estudianteService.findAll());
        model.addAttribute("estudiante", new Estudiante());

        // Carga las listas de grados y secciones para los filtros del HTML
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
     */
    @PostMapping("/estudiantes/editar")
    public String editarEstudiante(@ModelAttribute Estudiante estudiante, RedirectAttributes redirectAttributes) {
        try {
            estudianteService.update(estudiante); // Suponiendo que tienes un método update en EstudianteService
            redirectAttributes.addFlashAttribute("success", "Estudiante actualizado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el estudiante: " + e.getMessage());
        }
        return "redirect:/director/gestion/estudiantes";
    }

    /**
     * Procesa la eliminación de un estudiante.
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
     * Muestra la página de gestión de conductas.
     */
    @GetMapping("/conductas")
    public String gestionConductas(Model model, HttpSession session) {
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
     * Procesa la eliminación de una conducta.
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
     * Activa una conducta.
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
     * Desactiva una conducta.
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