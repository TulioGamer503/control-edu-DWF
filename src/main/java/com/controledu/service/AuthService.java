package com.controledu.service;

import com.controledu.model.*;
import com.controledu.repository.DirectorRepository;
import com.controledu.repository.DocenteRepository;
import com.controledu.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Date;

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

    // ==========================================================
    // MÉTODOS AGREGADOS PARA EDICIÓN DE PERFIL Y CONTRASEÑA
    // ==========================================================

    /**
     * Actualiza la información básica del perfil del usuario (nombres y apellidos).
     * NOTA: Debes extender este método en el AuthController para incluir
     * los campos específicos (materia, grado, etc.) y pasarlos aquí si el formulario los envía.
     */
    public Object updateUserProfile(Object currentUsuario, String nombres, String apellidos) throws Exception {

        if (currentUsuario instanceof Director director) {
            director.setNombres(nombres);
            director.setApellidos(apellidos);
            return directorRepository.save(director);

        } else if (currentUsuario instanceof Docente docente) {
            docente.setNombres(nombres);
            docente.setApellidos(apellidos);
            // Si el formulario enviara la materia, se actualizaría aquí: docente.setMateria(materia);
            return docenteRepository.save(docente);

        } else if (currentUsuario instanceof Estudiante estudiante) {
            estudiante.setNombres(nombres);
            estudiante.setApellidos(apellidos);
            // Si el formulario enviara otros campos (grado, seccion, fechaNacimiento), se actualizarían aquí.
            return estudianteRepository.save(estudiante);

        } else {
            throw new Exception("Tipo de usuario no reconocido o perfil no editable.");
        }
    }

    /**
     * Cambia la contraseña del usuario tras verificar la contraseña actual.
     * ADVERTENCIA: Este código usa comparación de contraseñas en texto plano.
     * En producción, usa un PasswordEncoder seguro (ej. BCrypt).
     */
    public void changePassword(Object currentUsuario, String currentPassword, String newPassword) throws Exception {

        if (currentUsuario instanceof Director director) {
            if (!director.getPassword().equals(currentPassword)) {
                throw new Exception("Contraseña actual incorrecta.");
            }
            director.setPassword(newPassword); // ¡ADVERTENCIA: Aquí debería ir el hash!
            directorRepository.save(director);

        } else if (currentUsuario instanceof Docente docente) {
            if (!docente.getPassword().equals(currentPassword)) {
                throw new Exception("Contraseña actual incorrecta.");
            }
            docente.setPassword(newPassword);
            docenteRepository.save(docente);

        } else if (currentUsuario instanceof Estudiante estudiante) {
            if (!estudiante.getPassword().equals(currentPassword)) {
                throw new Exception("Contraseña actual incorrecta.");
            }
            estudiante.setPassword(newPassword);
            estudianteRepository.save(estudiante);

        } else {
            throw new Exception("Tipo de usuario no reconocido para el cambio de contraseña.");
        }
    }
}