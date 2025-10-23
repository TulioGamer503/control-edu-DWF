package com.controledu.service;

import com.controledu.model.*;
import com.controledu.repository.ConductaRepository;
import com.controledu.repository.DocenteRepository;
import com.controledu.repository.EstudianteRepository;
import com.controledu.repository.RegistroConductaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistroConductaService {

    private final RegistroConductaRepository registroConductaRepository;
    private final EstudianteRepository estudianteRepository;
    private final DocenteRepository docenteRepository;
    private final ConductaRepository conductaRepository;

    public List<RegistroConducta> findAll() { return registroConductaRepository.findAll(); }
    public Optional<RegistroConducta> findById(Long id) { return registroConductaRepository.findById(id); }
    public List<RegistroConducta> findByEstudianteId(Long estudianteId) { return registroConductaRepository.findByEstudianteIdOrderByFechaRegistroDesc(estudianteId); }
    public List<RegistroConducta> findByDocenteId(Long docenteId) { return registroConductaRepository.findByDocenteIdOrderByFechaRegistroDesc(docenteId); }
    public List<RegistroConducta> findByConductaId(Long conductaId) { return registroConductaRepository.findByConductaIdConductaOrderByFechaRegistroDesc(conductaId); }
    public List<RegistroConducta> findByFecha(LocalDate fecha) { return registroConductaRepository.findByFechaRegistro(fecha); }
    public List<RegistroConducta> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) { return registroConductaRepository.findByFechaRegistroBetween(fechaInicio, fechaFin); }
    public List<RegistroConducta> findNoLeidos() { return registroConductaRepository.findByLeidoFalse(); }

    @Transactional
    public RegistroConducta registrarIncidente(Long estudianteId, Long conductaId, Long docenteId, String observaciones) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId).orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        Docente docente = docenteRepository.findById(docenteId).orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        Conducta conducta = conductaRepository.findById(conductaId).orElseThrow(() -> new RuntimeException("Conducta no encontrada"));

        RegistroConducta registro = new RegistroConducta();
        registro.setEstudiante(estudiante);
        registro.setDocente(docente);
        registro.setConducta(conducta);
        registro.setObservaciones(observaciones);
        registro.setFechaRegistro(LocalDate.now());
        registro.setLeido(false);
        registro.setEstado("ACTIVO");

        return registroConductaRepository.save(registro);
    }

    @Transactional
    public Optional<RegistroConducta> marcarComoLeido(Long id) {
        return registroConductaRepository.findById(id).map(registro -> {
            registro.setLeido(true);
            registro.setFechaLectura(LocalDate.now());
            return registroConductaRepository.save(registro);
        });
    }

    @Transactional
    public Optional<RegistroConducta> cambiarEstado(Long id, String estado) {
        return registroConductaRepository.findById(id).map(registro -> {
            registro.setEstado(estado);
            return registroConductaRepository.save(registro);
        });
    }

    public void deleteById(Long id) { registroConductaRepository.deleteById(id); }
    public long count() { return registroConductaRepository.count(); }
    public long countByEstado(String estado) { return registroConductaRepository.countByEstado(estado); }
    public long countByLeido(boolean leido) { return registroConductaRepository.countByLeido(leido); }
    public long countByDocenteId(Long docenteId) { return registroConductaRepository.countByDocenteId(docenteId); }

    // ✅ MÉTODO AÑADIDO
    public long countByEstudianteId(Long estudianteId) {
        return registroConductaRepository.countByEstudianteId(estudianteId);
    }

    public List<RegistroConducta> findRecent(int count) {
        if (count == 5) {
            return registroConductaRepository.findTop5ByOrderByFechaRegistroDesc();
        }
        return registroConductaRepository.findTop5ByOrderByFechaRegistroDesc();
    }

    public List<Object[]> countByGravedad() { return registroConductaRepository.countByGravedad(); }
    public List<Object[]> countByGrado() { return registroConductaRepository.countByGrado(); }
    public List<Object[]> countByMes() { return registroConductaRepository.countByMes(); }
}