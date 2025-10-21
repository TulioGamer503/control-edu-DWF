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

    @Transactional
    public Observacion save(Observacion observacion) {
        return observacionRepository.save(observacion);
    }

    @Transactional
    public Observacion registrarObservacion(Observacion observacion, Long estudianteId, Long docenteId) {
        Optional<Estudiante> estudiante = estudianteRepository.findById(estudianteId);
        Optional<Docente> docente = docenteRepository.findById(docenteId);

        if (estudiante.isEmpty() || docente.isEmpty()) {
            throw new RuntimeException("Estudiante o docente no encontrado");
        }

        observacion.setEstudiante(estudiante.get());
        observacion.setDocente(docente.get());
        observacion.setFecha(LocalDate.now());
        observacion.setLeido(false);

        return observacionRepository.save(observacion);
    }

    @Transactional
    public Optional<Observacion> marcarComoLeida(Long id) {
        Optional<Observacion> observacionOpt = observacionRepository.findById(id);
        if (observacionOpt.isPresent()) {
            Observacion observacion = observacionOpt.get();
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

    public List<Observacion> findRecent(int limit) {
        return observacionRepository.findRecent(limit);
    }

    public List<Observacion> findRecentByDocenteId(Long docenteId, int limit) {
        return observacionRepository.findRecentByDocenteId(docenteId, limit);
    }

    public List<Object[]> countByTipoObservacion() {
        return observacionRepository.countByTipoObservacion();
    }
}