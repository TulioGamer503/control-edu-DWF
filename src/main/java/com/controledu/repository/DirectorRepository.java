package com.controledu.repository;

import com.controledu.model.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {

    Optional<Director> findByUsuario(String usuario);
    Optional<Director> findByUsuarioAndPassword(String usuario, String password);
    boolean existsByUsuario(String usuario);

    @Query("SELECT d FROM Director d ORDER BY d.id LIMIT 1")
    Optional<Director> findFirstDirector();

    @Query("SELECT COUNT(d) FROM Director d")
    long countDirectores();

    @Query("SELECT d FROM Director d WHERE d.nombres LIKE %:nombre% OR d.apellidos LIKE %:nombre%")
    Optional<Director> findByNombreContaining(String nombre);
}