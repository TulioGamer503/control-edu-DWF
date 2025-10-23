package com.controledu.repository;

import com.controledu.model.RegistroConducta;
import org.springframework.data.domain.Pageable; // <-- Importante para LIMIT
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroConductaRepository extends JpaRepository<RegistroConducta, Long> {

    // Spring Data JPA puede crear esta consulta solo con el nombre del método
    List<RegistroConducta> findByFechaRegistro(LocalDate fechaRegistro);
    List<RegistroConducta> findByFechaRegistroBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<RegistroConducta> findByLeidoFalse();
    List<RegistroConducta> findByLeidoTrue();
    List<RegistroConducta> findByEstado(String estado);
    long countByEstado(String estado);
    long countByLeido(boolean leido); // <-- MÉTODO AÑADIDO

    // Spring puede crear estas consultas complejas también por el nombre del método
    List<RegistroConducta> findByEstudianteIdOrderByFechaRegistroDesc(Long estudianteId);
    List<RegistroConducta> findByDocenteIdOrderByFechaRegistroDesc(Long docenteId);
    List<RegistroConducta> findByConductaIdConductaOrderByFechaRegistroDesc(Long conductaId);

    // Contadores
    long countByEstudianteId(Long estudianteId);
    long countByDocenteId(Long docenteId);
    @Query("SELECT COUNT(rc) FROM RegistroConducta rc WHERE rc.estudiante.id = :estudianteId AND rc.leido = false")
    long countNoLeidosByEstudianteId(@Param("estudianteId") Long estudianteId);

    // FORMA MÁS FLEXIBLE DE BUSCAR RECIENTES (LIMIT)
    List<RegistroConducta> findTop5ByOrderByFechaRegistroDesc();

    // Estadísticas
    @Query("SELECT rc.conducta.gravedad.nombreGravedad, COUNT(rc) FROM RegistroConducta rc GROUP BY rc.conducta.gravedad.nombreGravedad")
    List<Object[]> countByGravedad();

    @Query("SELECT rc.estudiante.grado, COUNT(rc) FROM RegistroConducta rc GROUP BY rc.estudiante.grado")
    List<Object[]> countByGrado();

    @Query("SELECT YEAR(rc.fechaRegistro), MONTH(rc.fechaRegistro), COUNT(rc) FROM RegistroConducta rc GROUP BY YEAR(rc.fechaRegistro), MONTH(rc.fechaRegistro)")
    List<Object[]> countByMes();
}