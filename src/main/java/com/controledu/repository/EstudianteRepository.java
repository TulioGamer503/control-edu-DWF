package com.controledu.repository;

import com.controledu.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Métodos básicos
    Optional<Estudiante> findByUsuario(String usuario);
    Optional<Estudiante> findByUsuarioAndPassword(String usuario, String password);
    boolean existsByUsuario(String usuario);

    // Métodos específicos
    List<Estudiante> findByGradoAndSeccion(String grado, String seccion);
    List<Estudiante> findByGrado(String grado);
    List<Estudiante> findBySeccion(String seccion);

    @Query("SELECT e FROM Estudiante e WHERE e.nombres LIKE %:nombre% OR e.apellidos LIKE %:nombre%")
    List<Estudiante> findByNombreContaining(@Param("nombre") String nombre);

    long countByGradoAndSeccion(String grado, String seccion);
    long countByGrado(String grado);

    @Query("SELECT DISTINCT e.grado FROM Estudiante e ORDER BY e.grado")
    List<String> findAllGradosDistinct();

    @Query("SELECT DISTINCT e.seccion FROM Estudiante e ORDER BY e.seccion")
    List<String> findAllSeccionesDistinct();

    @Query("SELECT e, COUNT(rc) as totalIncidencias " +
            "FROM Estudiante e LEFT JOIN RegistroConducta rc ON e.id = rc.estudiante.id " +
            "GROUP BY e.id, e.nombres, e.apellidos " +
            "ORDER BY totalIncidencias DESC")
    List<Object[]> findEstudiantesConMasIncidencias();

    @Query("SELECT e FROM Estudiante e WHERE e.id NOT IN " +
            "(SELECT rc.estudiante.id FROM RegistroConducta rc)")
    List<Estudiante> findEstudiantesSinIncidencias();

    // Métodos adicionales útiles
    List<Estudiante> findAllByOrderByNombresAsc();

    @Query("SELECT COUNT(e) FROM Estudiante e")
    long countEstudiantes();
}