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
        return estudianteRepository.save(estudiante);
    }

    /**
     * Actualiza un estudiante existente en la base de datos.
     * @param estudianteActualizado El objeto Estudiante con los nuevos datos.
     * @return El estudiante guardado.
     */
    public Estudiante update(Estudiante estudianteActualizado) {
        // Busca el estudiante existente en la base de datos por su ID.
        Estudiante estudianteExistente = estudianteRepository.findById(estudianteActualizado.getId())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + estudianteActualizado.getId()));

        // Actualiza todos los campos con los nuevos valores.
        estudianteExistente.setNombres(estudianteActualizado.getNombres());
        estudianteExistente.setApellidos(estudianteActualizado.getApellidos());
        estudianteExistente.setGrado(estudianteActualizado.getGrado());
        estudianteExistente.setSeccion(estudianteActualizado.getSeccion());
        estudianteExistente.setFechaNacimiento(estudianteActualizado.getFechaNacimiento());
        estudianteExistente.setUsuario(estudianteActualizado.getUsuario());

        // Lógica especial para la contraseña: solo se actualiza si el campo no viene vacío.
        if (estudianteActualizado.getPassword() != null && !estudianteActualizado.getPassword().isEmpty()) {
            estudianteExistente.setPassword(estudianteActualizado.getPassword());
        }

        // Guarda el estudiante ya actualizado.
        return estudianteRepository.save(estudianteExistente);
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

    public List<String> findAllGradosDistinct() { // <-- El cambio está aquí
        return estudianteRepository.findAllGradosDistinct();
    }

    // Este método para las secciones ya es correcto porque las secciones son texto ("A", "B", etc.)
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