package com.controledu.service;

import com.controledu.model.Estudiante;
import com.controledu.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    public List<Estudiante> findAll() {
        return estudianteRepository.findAll();
    }

    public Optional<Estudiante> findById(Long id) {
        return estudianteRepository.findById(id);
    }

    public Optional<Estudiante> findByUsuario(String usuario) {
        return estudianteRepository.findByUsuario(usuario);
    }

    public List<Estudiante> findByGradoAndSeccion(String grado, String seccion) {
        return estudianteRepository.findByGradoAndSeccion(grado, seccion);
    }

    public List<Estudiante> findByGrado(String grado) {
        return estudianteRepository.findByGrado(grado);
    }

    public List<Estudiante> findBySeccion(String seccion) {
        return estudianteRepository.findBySeccion(seccion);
    }

    public List<Estudiante> findByNombreContaining(String nombre) {
        return estudianteRepository.findByNombreContaining(nombre);
    }

    public Estudiante save(Estudiante estudiante) {
        // ELIMINADO: estudiante.setRol("ESTUDIANTE"); - Ya no es necesario
        return estudianteRepository.save(estudiante);
    }

    public void deleteById(Long id) {
        estudianteRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return estudianteRepository.existsById(id);
    }

    public boolean existsByUsuario(String usuario) {
        return estudianteRepository.existsByUsuario(usuario);
    }

    public long count() {
        return estudianteRepository.count();
    }

    public long countByGradoAndSeccion(String grado, String seccion) {
        return estudianteRepository.countByGradoAndSeccion(grado, seccion);
    }

    public long countByGrado(String grado) {
        return estudianteRepository.countByGrado(grado);
    }

    public List<String> findAllGradosDistinct() {
        return estudianteRepository.findAllGradosDistinct();
    }

    public List<String> findAllSeccionesDistinct() {
        return estudianteRepository.findAllSeccionesDistinct();
    }

    public List<Object[]> findEstudiantesConMasIncidencias() {
        return estudianteRepository.findEstudiantesConMasIncidencias();
    }

    public List<Estudiante> findEstudiantesSinIncidencias() {
        return estudianteRepository.findEstudiantesSinIncidencias();
    }
}