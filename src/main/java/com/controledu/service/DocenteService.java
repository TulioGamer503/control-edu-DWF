package com.controledu.service;

import com.controledu.model.Docente;
import com.controledu.repository.DocenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocenteService {

    private final DocenteRepository docenteRepository;

    public List<Docente> findAll() {
        return docenteRepository.findAll();
    }

    public Optional<Docente> findById(Long id) {
        return docenteRepository.findById(id);
    }

    public Optional<Docente> findByUsuario(String usuario) {
        return docenteRepository.findByUsuario(usuario);
    }

    public List<Docente> findByMateria(String materia) {
        return docenteRepository.findByMateria(materia);
    }

    public List<Docente> findByNombreContaining(String nombre) {
        return docenteRepository.findByNombreContaining(nombre);
    }

    public Docente save(Docente docente) {
        return docenteRepository.save(docente);
    }

    public Docente update(Docente docenteActualizado) {
        Docente docenteExistente = docenteRepository.findById(docenteActualizado.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con id: " + docenteActualizado.getId()));

        docenteExistente.setNombres(docenteActualizado.getNombres());
        docenteExistente.setApellidos(docenteActualizado.getApellidos());
        docenteExistente.setMateria(docenteActualizado.getMateria());
        docenteExistente.setUsuario(docenteActualizado.getUsuario());

        // Lógica especial para la contraseña: solo se actualiza si el campo no viene vacío.
        if (docenteActualizado.getPassword() != null && !docenteActualizado.getPassword().isEmpty()) {
            // Aquí deberías encriptar la contraseña antes de guardarla en un proyecto real
            docenteExistente.setPassword(docenteActualizado.getPassword());
        }

        return docenteRepository.save(docenteExistente);
    }

    public void deleteById(Long id) {
        docenteRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return docenteRepository.existsById(id);
    }

    public boolean existsByUsuario(String usuario) {
        return docenteRepository.existsByUsuario(usuario);
    }

    public long count() {
        return docenteRepository.count();
    }

    public long countByMateria(String materia) {
        return docenteRepository.countByMateria(materia);
    }

    public List<String> findAllMateriasDistinct() {
        return docenteRepository.findAllMateriasDistinct();
    }

    public List<Object[]> findDocentesConMasRegistros() {
        return docenteRepository.findDocentesConMasRegistros();
    }
}