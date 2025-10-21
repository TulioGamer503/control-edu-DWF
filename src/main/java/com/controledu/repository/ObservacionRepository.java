package com.controledu.repository;

import com.controledu.model.Observacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObservacionRepository extends JpaRepository<Observacion, Long> {

    @Query("SELECT o FROM Observacion o WHERE o.estudiante.id = :estudianteId ORDER BY o.fecha DESC")
    List<Observacion> findByEstudianteId(@Param("estudianteId") Long estudianteId);

    @Query("SELECT o FROM Observacion o WHERE o.docente.id = :docenteId ORDER BY o.fecha DESC")
    List<Observacion> findByDocenteId(@Param("docenteId") Long docenteId);

    List<Observacion> findByTipoObservacion(String tipoObservacion);
    List<Observacion> findByFecha(LocalDate fecha);

    @Query("SELECT o FROM Observacion o WHERE o.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY o.fecha DESC")
    List<Observacion> findByFechaBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                         @Param("fechaFin") LocalDate fechaFin);

    List<Observacion> findByLeidoFalse();
    List<Observacion> findByLeidoTrue();

    @Query("SELECT o FROM Observacion o WHERE o.estudiante.id = :estudianteId AND o.leido = false")
    List<Observacion> findNoLeidasByEstudianteId(@Param("estudianteId") Long estudianteId);

    @Query("SELECT COUNT(o) FROM Observacion o WHERE o.estudiante.id = :estudianteId")
    long countByEstudianteId(@Param("estudianteId") Long estudianteId);

    @Query("SELECT COUNT(o) FROM Observacion o WHERE o.docente.id = :docenteId")
    long countByDocenteId(@Param("docenteId") Long docenteId);

    @Query("SELECT COUNT(o) FROM Observacion o")
    long countObservaciones();

    @Query("SELECT COUNT(o) FROM Observacion o WHERE o.estudiante.id = :estudianteId AND o.leido = false")
    long countNoLeidasByEstudianteId(@Param("estudianteId") Long estudianteId);

    @Modifying
    @Query("UPDATE Observacion o SET o.leido = true, o.fechaLectura = CURRENT_DATE WHERE o.idObservacion = :id")
    void marcarComoLeida(@Param("id") Long id);

    @Query("SELECT o FROM Observacion o ORDER BY o.fecha DESC, o.idObservacion DESC LIMIT :limit")
    List<Observacion> findRecent(@Param("limit") int limit);

    @Query("SELECT o FROM Observacion o WHERE o.docente.id = :docenteId ORDER BY o.fecha DESC, o.idObservacion DESC LIMIT :limit")
    List<Observacion> findRecentByDocenteId(@Param("docenteId") Long docenteId, @Param("limit") int limit);

    @Query("SELECT o.tipoObservacion, COUNT(o) FROM Observacion o GROUP BY o.tipoObservacion")
    List<Object[]> countByTipoObservacion();
}