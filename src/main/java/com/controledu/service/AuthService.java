package com.controledu.service;

import com.controledu.model.*;
import com.controledu.repository.DirectorRepository;
import com.controledu.repository.DocenteRepository;
import com.controledu.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final DirectorRepository directorRepository;
    private final DocenteRepository docenteRepository;
    private final EstudianteRepository estudianteRepository;

    public Object authenticate(String usuario, String password) {
        System.out.println("Intentando autenticar: " + usuario);

        // Buscar en todas las tablas
        Optional<Director> director = directorRepository.findByUsuarioAndPassword(usuario, password);
        if (director.isPresent()) {
            System.out.println("Director encontrado: " + director.get().getNombreCompleto());
            return director.get();
        }

        Optional<Docente> docente = docenteRepository.findByUsuarioAndPassword(usuario, password);
        if (docente.isPresent()) {
            System.out.println("Docente encontrado: " + docente.get().getNombreCompleto());
            return docente.get();
        }

        Optional<Estudiante> estudiante = estudianteRepository.findByUsuarioAndPassword(usuario, password);
        if (estudiante.isPresent()) {
            System.out.println("Estudiante encontrado: " + estudiante.get().getNombreCompleto());
            return estudiante.get();
        }

        System.out.println("No se encontró usuario con esas credenciales");
        return null;
    }

    public String getUserRole(Object user) {
        if (user instanceof Director) return "DIRECTOR";
        if (user instanceof Docente) return "DOCENTE";
        if (user instanceof Estudiante) return "ESTUDIANTE";
        return null;
    }
}