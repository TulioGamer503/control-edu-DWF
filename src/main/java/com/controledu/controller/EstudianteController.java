package com.controledu.controller;

import com.controledu.model.Estudiante;
import com.controledu.model.RegistroConducta;
import com.controledu.model.Observacion;
import com.controledu.service.RegistroConductaService;
import com.controledu.service.ObservacionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors; // <-- 1. IMPORTA COLLECTORS

/**
 * Controlador MVC para el rol ESTUDIANTE.
 * Provee endpoints para:
 * - Dashboard con métricas (faltas/observaciones) del estudiante autenticado.
 * - Historial unificado (faltas + observaciones) ordenado por fecha.
 * - Vistas separadas de conductas y observaciones con filtros/contadores.
 * - Perfil del estudiante.
 *
 * Seguridad básica:
 * - Todos los métodos verifican que exista un Estudiante en sesión (atributo "usuario").
 */
@Controller
@RequestMapping("/estudiante")
@RequiredArgsConstructor
public class EstudianteController {

    // Servicios de dominio que exponen consultas y operaciones de negocio.
    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService;

    // --- DASHBOARD ---
    /**
     * Muestra el panel principal del estudiante con KPIs y últimos registros.
     * Requiere que el estudiante esté autenticado (presente en sesión).
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Recupera el estudiante autenticado desde la sesión HTTP
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            // Si no hay sesión válida, redirige a login
            return "redirect:/auth/login";
        }

        // Métricas por estudiante
        long totalFaltas = registroConductaService.countByEstudianteId(estudiante.getId());
        long totalObservaciones = observacionService.countByEstudianteId(estudiante.getId());

        // Listado (podría limitarse a “recientes” desde el servicio si se requiere)
        List<RegistroConducta> faltasRecientes = registroConductaService.findByEstudianteId(estudiante.getId());
        if (faltasRecientes == null) {
            faltasRecientes = new ArrayList<>();
        }

        // Atributos para la vista
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("totalFaltas", totalFaltas);
        model.addAttribute("totalObservaciones", totalObservaciones);
        model.addAttribute("faltasRecientes", faltasRecientes);

        return "estudiante/dashboard";
    }

    // --- HISTORIAL ---
    /**
     * Genera una línea de tiempo (timeline) combinando faltas y observaciones del estudiante.
     * Los elementos se ordenan por fecha descendente.
     */
    @GetMapping("/historial")
    public String historial(HttpSession session, Model model) {
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            return "redirect:/auth/login";
        }

        // Cargar registros asociados al estudiante
        List<RegistroConducta> conductas = registroConductaService.findByEstudianteId(estudiante.getId());
        List<Observacion> observaciones = observacionService.findByEstudianteId(estudiante.getId());

        // Evitar NPE utilizando listas vacías cuando sea necesario
        if (conductas == null) { conductas = new ArrayList<>(); }
        if (observaciones == null) { observaciones = new ArrayList<>(); }

        // Cálculos de totales y por severidad (defensa ante nulos en el grafo de objetos)
        long totalFaltas = conductas.size();
        long faltasLeves = conductas.stream()
                .filter(c -> c.getConducta() != null && c.getConducta().getGravedad() != null && c.getConducta().getGravedad().getNombreGravedad() != null &&
                        c.getConducta().getGravedad().getNombreGravedad().equalsIgnoreCase("leve"))
                .count();
        long faltasGraves = conductas.stream()
                .filter(c -> c.getConducta() != null && c.getConducta().getGravedad() != null && c.getConducta().getGravedad().getNombreGravedad() != null &&
                        c.getConducta().getGravedad().getNombreGravedad().equalsIgnoreCase("grave"))
                .count();
        long faltasMuyGraves = conductas.stream()
                .filter(c -> c.getConducta() != null && c.getConducta().getGravedad() != null && c.getConducta().getGravedad().getNombreGravedad() != null &&
                        c.getConducta().getGravedad().getNombreGravedad().equalsIgnoreCase("muy grave"))
                .count();

        // Unir faltas y observaciones en una sola lista heterogénea
        List<Object> timelineItems = new ArrayList<>();
        timelineItems.addAll(conductas);
        timelineItems.addAll(observaciones);

        // Ordenar por fecha descendente (si usas LocalDateTime, ajusta el comparador)
        timelineItems.sort(Comparator.comparing(item -> {
            if (item instanceof RegistroConducta) {
                return ((RegistroConducta) item).getFechaRegistro();
            } else if (item instanceof Observacion) {
                return ((Observacion) item).getFecha();
            }
            return null;
        }, Comparator.nullsLast(LocalDate::compareTo).reversed()));

        // Exponer datos a la vista
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("totalFaltas", totalFaltas);
        model.addAttribute("totalFaltasLeves", faltasLeves);
        model.addAttribute("totalFaltasGraves", faltasGraves);
        model.addAttribute("totalFaltasMuyGraves", faltasMuyGraves);
        model.addAttribute("timelineItems", timelineItems);

        return "estudiante/historial";
    }

    // --- 2. MÉTODO NUEVO: MIS CONDUCTAS ---
    /**
     * Vista específica para listar las faltas del estudiante, separadas por nivel de gravedad.
     */
    @GetMapping("/conductas")
    public String misConductas(HttpSession session, Model model) {
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            return "redirect:/auth/login";
        }

        // Recuperar todas las conductas del estudiante
        List<RegistroConducta> todasLasConductas = registroConductaService.findByEstudianteId(estudiante.getId());
        if (todasLasConductas == null) {
            todasLasConductas = new ArrayList<>();
        }

        // Filtrar por severidad (con chequeos nulos defensivos)
        List<RegistroConducta> conductasLeves = todasLasConductas.stream()
                .filter(c -> c.getConducta() != null && c.getConducta().getGravedad() != null && c.getConducta().getGravedad().getNombreGravedad() != null &&
                        c.getConducta().getGravedad().getNombreGravedad().equalsIgnoreCase("leve"))
                .collect(Collectors.toList());

        List<RegistroConducta> conductasGraves = todasLasConductas.stream()
                .filter(c -> c.getConducta() != null && c.getConducta().getGravedad() != null && c.getConducta().getGravedad().getNombreGravedad() != null &&
                        c.getConducta().getGravedad().getNombreGravedad().equalsIgnoreCase("grave"))
                .collect(Collectors.toList());

        List<RegistroConducta> conductasMuyGraves = todasLasConductas.stream()
                .filter(c -> c.getConducta() != null && c.getConducta().getGravedad() != null && c.getConducta().getGravedad().getNombreGravedad() != null &&
                        c.getConducta().getGravedad().getNombreGravedad().equalsIgnoreCase("muy grave"))
                .collect(Collectors.toList());

        // Datos para la vista
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("conductasLeves", conductasLeves);
        model.addAttribute("conductasGraves", conductasGraves);
        model.addAttribute("conductasMuyGraves", conductasMuyGraves);

        return "estudiante/conductas";
    }

    // --- 3. MÉTODO NUEVO: MIS OBSERVACIONES ---
    /**
     * Vista específica para listar las observaciones del estudiante clasificadas por tipo.
     * Calcula también los totales por categoría (positiva/negativa/otras).
     */
    @GetMapping("/observaciones")
    public String misObservaciones(HttpSession session, Model model) {
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            return "redirect:/auth/login";
        }

        // 1. Obtener todas las observaciones del estudiante
        List<Observacion> observaciones = observacionService.findByEstudianteId(estudiante.getId());
        if (observaciones == null) {
            observaciones = new ArrayList<>();
        }

        // --- 2. LÓGICA DE FILTRADO Y CONTEO (MOVIDA DESDE EL HTML) ---

        // Clasificar observaciones por tipo (defensivo ante nulos)
        List<Observacion> observacionesPositivas = observaciones.stream()
                .filter(o -> o.getTipoObservacion() != null && o.getTipoObservacion().equalsIgnoreCase("positiva"))
                .collect(Collectors.toList());

        List<Observacion> observacionesNegativas = observaciones.stream()
                .filter(o -> o.getTipoObservacion() != null && o.getTipoObservacion().equalsIgnoreCase("negativa"))
                .collect(Collectors.toList());

        List<Observacion> observacionesOtras = observaciones.stream()
                .filter(o -> o.getTipoObservacion() != null &&
                        !o.getTipoObservacion().equalsIgnoreCase("positiva") &&
                        !o.getTipoObservacion().equalsIgnoreCase("negativa"))
                .collect(Collectors.toList());

        // Totales por categoría
        long totalPositivas = observacionesPositivas.size();
        long totalNegativas = observacionesNegativas.size();
        long totalOtras = observacionesOtras.size();

        // --- FIN DE LA LÓGICA ---

        // 3. Exponer resultados a la vista
        model.addAttribute("estudiante", estudiante);

        // Listas filtradas
        model.addAttribute("observacionesPositivas", observacionesPositivas);
        model.addAttribute("observacionesNegativas", observacionesNegativas);
        model.addAttribute("observacionesOtras", observacionesOtras);

        // Cuentas
        model.addAttribute("totalPositivas", totalPositivas);
        model.addAttribute("totalNegativas", totalNegativas);
        model.addAttribute("totalOtras", totalOtras);

        // Lista original (por si la plantilla la requiere)
        model.addAttribute("observaciones", observaciones);

        return "estudiante/observaciones";
    }

    // --- 4. MÉTODO NUEVO: MI PERFIL ---
    /**
     * Muestra la vista del perfil del estudiante autenticado.
     * No realiza consultas adicionales: usa el objeto de sesión.
     */
    @GetMapping("/perfil")
    public String miPerfil(HttpSession session, Model model) {
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            return "redirect:/auth/login";
        }

        // Adjunta el estudiante para renderizar su información en la vista
        model.addAttribute("estudiante", estudiante);

        return "estudiante/perfil";
    }
}
