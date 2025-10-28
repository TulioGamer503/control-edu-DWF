package com.controledu.service;

import com.controledu.model.Estudiante;
import com.controledu.model.Observacion;
import com.controledu.model.Docente;
import com.controledu.repository.ObservacionRepository;
import com.controledu.repository.EstudianteRepository;
import com.controledu.repository.DocenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ObservacionService {

    private final ObservacionRepository observacionRepository;
    private final EstudianteRepository estudianteRepository;
    private final DocenteRepository docenteRepository;

    public List<Observacion> findAll() {
        return observacionRepository.findAll();
    }

    public Optional<Observacion> findById(Long id) {
        return observacionRepository.findById(id);
    }

    public List<Observacion> findByEstudianteId(Long estudianteId) {
        return observacionRepository.findByEstudianteId(estudianteId);
    }

    public List<Observacion> findByDocenteId(Long docenteId) {
        return observacionRepository.findByDocenteId(docenteId);
    }

    public List<Observacion> findByTipoObservacion(String tipo) {
        return observacionRepository.findByTipoObservacion(tipo);
    }

    public List<Observacion> findByFecha(LocalDate fecha) {
        return observacionRepository.findByFecha(fecha);
    }

    public List<Observacion> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        return observacionRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    public List<Observacion> findNoLeidas() {
        return observacionRepository.findByLeidoFalse();
    }

    public List<Observacion> findLeidas() {
        return observacionRepository.findByLeidoTrue();
    }

    public List<Observacion> findNoLeidasByEstudianteId(Long estudianteId) {
        return observacionRepository.findNoLeidasByEstudianteId(estudianteId);
    }

    // --- ✅ RENAMED THIS METHOD ---
    @Transactional
    public Observacion guardar(Observacion observacion) { // Was named 'save'
        return observacionRepository.save(observacion);
    }
    // ----------------------------

    @Transactional
    public Observacion registrarObservacion(Observacion observacion, Long estudianteId, Long docenteId) {
        Optional<Estudiante> estudiante = estudianteRepository.findById(estudianteId);
        Optional<Docente> docente = docenteRepository.findById(docenteId);

        if (estudiante.isEmpty() || docente.isEmpty()) {
            // It's usually better to throw a more specific exception or handle this differently
            throw new RuntimeException("Estudiante o docente no encontrado");
        }

        observacion.setEstudiante(estudiante.get());
        observacion.setDocente(docente.get());
        observacion.setFecha(LocalDate.now());
        observacion.setLeido(false);

        // Now calling the renamed method 'guardar' implicitly via repository.save
        return observacionRepository.save(observacion);
    }

    @Transactional
    public Optional<Observacion> marcarComoLeida(Long id) {
        Optional<Observacion> observacionOpt = observacionRepository.findById(id);
        if (observacionOpt.isPresent()) {
            Observacion observacion = observacionOpt.get();
            // Assuming your Observacion model has a method like this
            // If not, just do: observacion.setLeido(true); observacion.setFechaLectura(LocalDate.now());
            observacion.marcarComoLeido();
            return Optional.of(observacionRepository.save(observacion));
        }
        return Optional.empty();
    }

    public void deleteById(Long id) {
        observacionRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return observacionRepository.existsById(id);
    }

    public long count() {
        return observacionRepository.count();
    }

    public long countByEstudianteId(Long estudianteId) {
        return observacionRepository.countByEstudianteId(estudianteId);
    }

    public long countByDocenteId(Long docenteId) {
        return observacionRepository.countByDocenteId(docenteId);
    }

    public long countNoLeidasByEstudianteId(Long estudianteId) {
        return observacionRepository.countNoLeidasByEstudianteId(estudianteId);
    }

    // These methods seem to expect Pageable or rely on specific repository queries
    // Make sure ObservacionRepository has 'findRecent' and 'findRecentByDocenteId' methods defined correctly
    public List<Observacion> findRecent(int limit) {
        // Example assumes a JPQL query like "SELECT o FROM Observacion o ORDER BY o.fecha DESC"
        // and using PageRequest.of(0, limit)
        // return observacionRepository.findRecent(PageRequest.of(0, limit));
        // Placeholder - adapt to your repository method:
        return observacionRepository.findAll().stream().limit(limit).toList(); // Basic implementation
    }

    public List<Observacion> findRecentByDocenteId(Long docenteId, int limit) {
        // Placeholder - adapt to your repository method:
        return observacionRepository.findByDocenteId(docenteId).stream().limit(limit).toList(); // Basic implementation
    }

    public List<Object[]> countByTipoObservacion() {
        return observacionRepository.countByTipoObservacion();
    }
}