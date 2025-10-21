package com.controledu.service;

import com.controledu.model.Director;
import com.controledu.repository.DirectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;

    public List<Director> findAll() {
        return directorRepository.findAll();
    }

    public Optional<Director> findById(Long id) {
        return directorRepository.findById(id);
    }

    public Optional<Director> findByUsuario(String usuario) {
        return directorRepository.findByUsuario(usuario);
    }

    public Director save(Director director) {
        // ELIMINADO: director.setRol("DIRECTOR"); - Ya no es necesario
        return directorRepository.save(director);
    }

    public void deleteById(Long id) {
        directorRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return directorRepository.existsById(id);
    }

    public boolean existsByUsuario(String usuario) {
        return directorRepository.existsByUsuario(usuario);
    }

    public long count() {
        return directorRepository.count();
    }

    public Optional<Director> getFirstDirector() {
        return directorRepository.findFirstDirector();
    }
}