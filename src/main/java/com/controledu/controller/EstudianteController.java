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

@Controller
@RequestMapping("/estudiante")
@RequiredArgsConstructor
public class EstudianteController {

    private final RegistroConductaService registroConductaService;
    private final ObservacionService observacionService;

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            return "redirect:/auth/login";
        }

        long totalFaltas = registroConductaService.countByEstudianteId(estudiante.getId());
        long totalObservaciones = observacionService.countByEstudianteId(estudiante.getId());

        List<RegistroConducta> faltasRecientes = registroConductaService.findByEstudianteId(estudiante.getId());
        if (faltasRecientes == null) {
            faltasRecientes = new ArrayList<>();
        }

        model.addAttribute("estudiante", estudiante);
        model.addAttribute("totalFaltas", totalFaltas);
        model.addAttribute("totalObservaciones", totalObservaciones);
        model.addAttribute("faltasRecientes", faltasRecientes);

        return "estudiante/dashboard";
    }

    // --- HISTORIAL ---
    @GetMapping("/historial")
    public String historial(HttpSession session, Model model) {
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            return "redirect:/auth/login";
        }

        List<RegistroConducta> conductas = registroConductaService.findByEstudianteId(estudiante.getId());
        List<Observacion> observaciones = observacionService.findByEstudianteId(estudiante.getId());

        if (conductas == null) { conductas = new ArrayList<>(); }
        if (observaciones == null) { observaciones = new ArrayList<>(); }

        // Cálculos de totales
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

        // Unir y ordenar listas para la línea de tiempo
        List<Object> timelineItems = new ArrayList<>();
        timelineItems.addAll(conductas);
        timelineItems.addAll(observaciones);

        // (Ajusta LocalDate::compareTo si usas LocalDateTime)
        timelineItems.sort(Comparator.comparing(item -> {
            if (item instanceof RegistroConducta) {
                return ((RegistroConducta) item).getFechaRegistro();
            } else if (item instanceof Observacion) {
                return ((Observacion) item).getFecha();
            }
            return null;
        }, Comparator.nullsLast(LocalDate::compareTo).reversed()));

        // Agregar todo al modelo
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("totalFaltas", totalFaltas);
        model.addAttribute("totalFaltasLeves", faltasLeves);
        model.addAttribute("totalFaltasGraves", faltasGraves);
        model.addAttribute("totalFaltasMuyGraves", faltasMuyGraves);
        model.addAttribute("timelineItems", timelineItems);

        return "estudiante/historial";
    }

    // --- 2. MÉTODO NUEVO: MIS CONDUCTAS ---
    @GetMapping("/conductas")
    public String misConductas(HttpSession session, Model model) {
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            return "redirect:/auth/login";
        }

        // Obtener todas las conductas
        List<RegistroConducta> todasLasConductas = registroConductaService.findByEstudianteId(estudiante.getId());
        if (todasLasConductas == null) {
            todasLasConductas = new ArrayList<>();
        }

        // Filtrar las listas (a prueba de nulos)
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

        // Agregar al modelo
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("conductasLeves", conductasLeves);
        model.addAttribute("conductasGraves", conductasGraves);
        model.addAttribute("conductasMuyGraves", conductasMuyGraves);

        return "estudiante/conductas";
    }

    // --- 3. MÉTODO NUEVO: MIS OBSERVACIONES ---
    @GetMapping("/observaciones")
    public String misObservaciones(HttpSession session, Model model) {
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            return "redirect:/auth/login";
        }

        // 1. Obtener todas las observaciones
        List<Observacion> observaciones = observacionService.findByEstudianteId(estudiante.getId());
        if (observaciones == null) {
            observaciones = new ArrayList<>();
        }

        // --- 2. LÓGICA DE FILTRADO Y CONTEO (MOVIDA DESDE EL HTML) ---

        // Filtrar las listas (a prueba de nulos)
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

        // Contar los totales
        long totalPositivas = observacionesPositivas.size();
        long totalNegativas = observacionesNegativas.size();
        long totalOtras = observacionesOtras.size();

        // --- FIN DE LA LÓGICA ---

        // 3. Agregar todo al modelo
        model.addAttribute("estudiante", estudiante);

        // Listas filtradas
        model.addAttribute("observacionesPositivas", observacionesPositivas);
        model.addAttribute("observacionesNegativas", observacionesNegativas);
        model.addAttribute("observacionesOtras", observacionesOtras);

        // Cuentas
        model.addAttribute("totalPositivas", totalPositivas);
        model.addAttribute("totalNegativas", totalNegativas);
        model.addAttribute("totalOtras", totalOtras);

        // (La lista 'observaciones' original ya no es necesaria, pero la dejamos por si acaso)
        model.addAttribute("observaciones", observaciones);

        return "estudiante/observaciones";
    }

    // --- 4. MÉTODO NUEVO: MI PERFIL ---
    @GetMapping("/perfil")
    public String miPerfil(HttpSession session, Model model) {
        Estudiante estudiante = (Estudiante) session.getAttribute("usuario");
        if (estudiante == null) {
            return "redirect:/auth/login";
        }

        // Solo necesitamos el estudiante, ya está en la sesión
        model.addAttribute("estudiante", estudiante);

        return "estudiante/perfil";
    }
}