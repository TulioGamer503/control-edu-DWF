package com.controledu.service;

import com.controledu.model.*;
import com.controledu.repository.RegistroConductaRepository;
import com.controledu.repository.EstudianteRepository;
import com.controledu.repository.DocenteRepository;
import com.controledu.repository.ConductaRepository;
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

    public List<RegistroConducta> findAll() {
        return registroConductaRepository.findAll();
    }

    public Optional<RegistroConducta> findById(Long id) {
        return registroConductaRepository.findById(id);
    }

    public List<RegistroConducta> findByEstudianteId(Long estudianteId) {
        return registroConductaRepository.findByEstudianteId(estudianteId);
    }

    public List<RegistroConducta> findByDocenteId(Long docenteId) {
        return registroConductaRepository.findByDocenteId(docenteId);
    }

    public List<RegistroConducta> findByConductaId(Long conductaId) {
        return registroConductaRepository.findByConductaId(conductaId);
    }

    public List<RegistroConducta> findByFecha(LocalDate fecha) {
        return registroConductaRepository.findByFechaRegistro(fecha);
    }

    public List<RegistroConducta> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return registroConductaRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);
    }

    public List<RegistroConducta> findByGravedadId(Long gravedadId) {
        return registroConductaRepository.findByGravedadId(gravedadId);
    }

    public List<RegistroConducta> findByEstudianteGrado(String grado) {
        return registroConductaRepository.findByEstudianteGrado(grado);
    }

    public List<RegistroConducta> findByEstudianteGradoAndSeccion(String grado, String seccion) {
        return registroConductaRepository.findByEstudianteGradoAndSeccion(grado, seccion);
    }

    public List<RegistroConducta> findNoLeidos() {
        return registroConductaRepository.findByLeidoFalse();
    }

    public List<RegistroConducta> findLeidos() {
        return registroConductaRepository.findByLeidoTrue();
    }

    public List<RegistroConducta> findNoLeidosByEstudianteId(Long estudianteId) {
        return registroConductaRepository.findNoLeidosByEstudianteId(estudianteId);
    }

    @Transactional
    public RegistroConducta save(RegistroConducta registroConducta) {
        return registroConductaRepository.save(registroConducta);
    }

    @Transactional
    public RegistroConducta registrarIncidente(RegistroConducta registroConducta, Long estudianteId, Long conductaId, Long docenteId) {
        Optional<Estudiante> estudiante = estudianteRepository.findById(estudianteId);
        Optional<Docente> docente = docenteRepository.findById(docenteId);
        Optional<Conducta> conducta = conductaRepository.findById(conductaId);

        if (estudiante.isEmpty() || docente.isEmpty() || conducta.isEmpty()) {
            throw new RuntimeException("Estudiante, docente o conducta no encontrado");
        }

        registroConducta.setEstudiante(estudiante.get());
        registroConducta.setDocente(docente.get());
        registroConducta.setConducta(conducta.get());
        registroConducta.setFechaRegistro(LocalDate.now());
        registroConducta.setLeido(false);
        registroConducta.setEstado("ACTIVO");

        return registroConductaRepository.save(registroConducta);
    }

    @Transactional
    public Optional<RegistroConducta> marcarComoLeido(Long id) {
        Optional<RegistroConducta> registroOpt = registroConductaRepository.findById(id);
        if (registroOpt.isPresent()) {
            RegistroConducta registro = registroOpt.get();
            registro.marcarComoLeido();
            return Optional.of(registroConductaRepository.save(registro));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<RegistroConducta> cambiarEstado(Long id, String estado) {
        Optional<RegistroConducta> registroOpt = registroConductaRepository.findById(id);
        if (registroOpt.isPresent()) {
            RegistroConducta registro = registroOpt.get();
            registro.setEstado(estado);
            return Optional.of(registroConductaRepository.save(registro));
        }
        return Optional.empty();
    }

    public void deleteById(Long id) {
        registroConductaRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return registroConductaRepository.existsById(id);
    }

    public long count() {
        return registroConductaRepository.count();
    }

    public long countByEstudianteId(Long estudianteId) {
        return registroConductaRepository.countByEstudianteId(estudianteId);
    }

    public long countByDocenteId(Long docenteId) {
        return registroConductaRepository.countByDocenteId(docenteId);
    }

    public long countNoLeidosByEstudianteId(Long estudianteId) {
        return registroConductaRepository.countNoLeidosByEstudianteId(estudianteId);
    }

    // Métodos usando Spring Data JPA para límites
    public List<RegistroConducta> findRecent() {
        return registroConductaRepository.findTop10ByOrderByFechaRegistroDescIdRegistroDesc();
    }

    public List<RegistroConducta> findRecentByDocenteId(Long docenteId) {
        return registroConductaRepository.findTop5ByDocenteIdOrderByFechaRegistroDescIdRegistroDesc(docenteId);
    }

    public List<RegistroConducta> findRecentByEstudianteId(Long estudianteId) {
        return registroConductaRepository.findTop5ByEstudianteIdOrderByFechaRegistroDescIdRegistroDesc(estudianteId);
    }

    // Métodos usando native queries para límites personalizados
    public List<RegistroConducta> findRecent(int limit) {
        return registroConductaRepository.findRecentNative(limit);
    }

    public List<RegistroConducta> findRecentByDocenteId(Long docenteId, int limit) {
        return registroConductaRepository.findRecentByDocenteIdNative(docenteId, limit);
    }

    public List<RegistroConducta> findRecentByEstudianteId(Long estudianteId, int limit) {
        return registroConductaRepository.findRecentByEstudianteIdNative(estudianteId, limit);
    }

    public List<Object[]> countByGravedad() {
        return registroConductaRepository.countByGravedad();
    }

    public List<Object[]> countByGrado() {
        return registroConductaRepository.countByGrado();
    }

    public List<Object[]> countByMes() {
        return registroConductaRepository.countByMes();
    }

    public List<RegistroConducta> findByEstado(String estado) {
        return registroConductaRepository.findByEstado(estado);
    }

    public long countByEstado(String estado) {
        return registroConductaRepository.countByEstado(estado);
    }
}