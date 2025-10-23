package com.controledu.controller;

import com.controledu.model.*;
import com.controledu.service.*;
import jakarta.servlet.http.HttpSession; // <-- AÑADIDO: Import para HttpSession
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/director/gestion")
@RequiredArgsConstructor
public class DirectorGestionController {

    private final DocenteService docenteService;
    private final EstudianteService estudianteService;
    private final ConductaService conductaService;
    private final TipoGravedadService tipoGravedadService;

    // ========== GESTIÓN DE DOCENTES ==========

    @GetMapping("/docentes")
    public String gestionDocentes(Model model, HttpSession session) { // <-- AÑADIDO: HttpSession session
        // --- INICIO CÓDIGO AÑADIDO ---
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login"; // Si no hay sesión, al login
        }
        model.addAttribute("director", director); // Poner director en el modelo
        // --- FIN CÓDIGO AÑADIDO ---

        List<Docente> docentes = docenteService.findAll();
        model.addAttribute("docentes", docentes);
        model.addAttribute("docente", new Docente()); // Para el formulario
        return "director/gestion-docentes"; // Nombre de tu HTML
    }

    @PostMapping("/docentes/crear")
    public String crearDocente(@ModelAttribute Docente docente,
                               RedirectAttributes redirectAttributes) {
        try {
            docenteService.save(docente);
            redirectAttributes.addFlashAttribute("success", "Docente creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear docente: " + e.getMessage());
        }
        return "redirect:/director/gestion/docentes";
    }

    @PostMapping("/docentes/editar")
    public String editarDocente(@ModelAttribute Docente docente,
                                RedirectAttributes redirectAttributes) {
        try {
            docenteService.save(docente);
            redirectAttributes.addFlashAttribute("success", "Docente actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar docente: " + e.getMessage());
        }
        return "redirect:/director/gestion/docentes";
    }

    @GetMapping("/docentes/eliminar/{id}")
    public String eliminarDocente(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            docenteService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Docente eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar docente: " + e.getMessage());
        }
        return "redirect:/director/gestion/docentes";
    }

    // ========== GESTIÓN DE ESTUDIANTES ==========

    @GetMapping("/estudiantes")
    public String gestionEstudiantes(Model model, HttpSession session) { // <-- AÑADIDO: HttpSession session
        // --- INICIO CÓDIGO AÑADIDO ---
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        // --- FIN CÓDIGO AÑADIDO ---

        List<Estudiante> estudiantes = estudianteService.findAll();
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("estudiante", new Estudiante()); // Para el formulario
        // Asegúrate de pasar también las listas de grados y secciones si tu HTML las necesita para los filtros
        model.addAttribute("grados", estudianteService.findAllGradosDistinct());
        model.addAttribute("secciones", estudianteService.findAllSeccionesDistinct());
        return "director/gestion-estudiantes"; // Nombre de tu HTML
    }

    @PostMapping("/estudiantes/crear")
    public String crearEstudiante(@ModelAttribute Estudiante estudiante,
                                  RedirectAttributes redirectAttributes) {
        try {
            estudianteService.save(estudiante);
            redirectAttributes.addFlashAttribute("success", "Estudiante creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear estudiante: " + e.getMessage());
        }
        return "redirect:/director/gestion/estudiantes";
    }

    @PostMapping("/estudiantes/editar")
    public String editarEstudiante(@ModelAttribute Estudiante estudiante,
                                   RedirectAttributes redirectAttributes) {
        try {
            estudianteService.save(estudiante);
            redirectAttributes.addFlashAttribute("success", "Estudiante actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar estudiante: " + e.getMessage());
        }
        return "redirect:/director/gestion/estudiantes";
    }

    @GetMapping("/estudiantes/eliminar/{id}")
    public String eliminarEstudiante(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            estudianteService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Estudiante eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar estudiante: " + e.getMessage());
        }
        return "redirect:/director/gestion/estudiantes";
    }

    // ========== GESTIÓN DE CONDUCTAS (REGLAS) ==========

    @GetMapping("/conductas")
    public String gestionConductas(Model model, HttpSession session) { // <-- AÑADIDO: HttpSession session
        // --- INICIO CÓDIGO AÑADIDO ---
        Director director = (Director) session.getAttribute("usuario");
        if (director == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("director", director);
        // --- FIN CÓDIGO AÑADIDO ---

        List<Conducta> conductas = conductaService.findAll();
        List<TipoGravedad> gravedades = tipoGravedadService.findAll();
        model.addAttribute("conductas", conductas);
        model.addAttribute("gravedades", gravedades);
        model.addAttribute("conducta", new Conducta()); // Para el formulario de añadir
        return "director/gestion-conductas"; // Nombre de tu HTML
    }

    @PostMapping("/conductas/crear")
    public String crearConducta(@ModelAttribute Conducta conducta,
                                @RequestParam Long idGravedad,
                                RedirectAttributes redirectAttributes) {
        try {
            conductaService.createConducta(
                    conducta.getNombreConducta(),
                    conducta.getDescripcion(),
                    idGravedad
            );
            redirectAttributes.addFlashAttribute("success", "Conducta creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear conducta: " + e.getMessage());
        }
        return "redirect:/director/gestion/conductas";
    }

    @PostMapping("/conductas/editar")
    public String editarConducta(@ModelAttribute Conducta conducta, // Asegúrate que el ID venga en el objeto conducta desde el form modal
                                 RedirectAttributes redirectAttributes) {
        try {
            // Podrías necesitar buscar la conducta existente y actualizarla en lugar de solo guardar
            // O si el objeto 'conducta' ya tiene el ID correcto, save() funcionará como update.
            conductaService.save(conducta);
            redirectAttributes.addFlashAttribute("success", "Conducta actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar conducta: " + e.getMessage());
        }
        return "redirect:/director/gestion/conductas";
    }

    @GetMapping("/conductas/eliminar/{id}")
    public String eliminarConducta(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Considera añadir validación para no eliminar si hay registros asociados
            conductaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Conducta eliminada exitosamente");
        } catch (Exception e) {
            // Captura específica si no se puede eliminar por dependencias
            redirectAttributes.addFlashAttribute("error", "Error al eliminar conducta: " + e.getMessage());
        }
        return "redirect:/director/gestion/conductas";
    }

    @GetMapping("/conductas/activar/{id}")
    public String activarConducta(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            conductaService.activarConducta(id);
            redirectAttributes.addFlashAttribute("success", "Conducta activada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar conducta: " + e.getMessage());
        }
        return "redirect:/director/gestion/conductas";
    }

    @GetMapping("/conductas/desactivar/{id}")
    public String desactivarConducta(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            conductaService.desactivarConducta(id);
            redirectAttributes.addFlashAttribute("success", "Conducta desactivada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar conducta: " + e.getMessage());
        }
        return "redirect:/director/gestion/conductas";
    }
}