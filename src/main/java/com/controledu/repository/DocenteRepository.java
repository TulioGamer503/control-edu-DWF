package com.controledu.repository;

import com.controledu.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {

    // Métodos básicos
    Optional<Docente> findByUsuario(String usuario);
    Optional<Docente> findByUsuarioAndPassword(String usuario, String password);
    boolean existsByUsuario(String usuario);

    // Métodos que estaban faltando
    List<Docente> findByMateria(String materia);

    List<Docente> findByMateriaContainingIgnoreCase(String materia);

    @Query("SELECT d FROM Docente d WHERE d.nombres LIKE %:nombre% OR d.apellidos LIKE %:nombre%")
    List<Docente> findByNombreContaining(@Param("nombre") String nombre);

    long countByMateria(String materia);

    @Query("SELECT DISTINCT d.materia FROM Docente d ORDER BY d.materia")
    List<String> findAllMateriasDistinct();

    @Query("SELECT d, COUNT(rc) as total " +
            "FROM Docente d LEFT JOIN RegistroConducta rc ON d.id = rc.docente.id " +
            "GROUP BY d.id, d.nombres, d.apellidos " +
            "ORDER BY total DESC")
    List<Object[]> findDocentesConMasRegistros();

    // Métodos adicionales útiles
    List<Docente> findAllByOrderByNombresAsc();

    @Query("SELECT COUNT(d) FROM Docente d")
    long countDocentes();
}