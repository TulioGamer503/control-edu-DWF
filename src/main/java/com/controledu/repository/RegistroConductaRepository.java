package com.controledu.repository;

import com.controledu.model.RegistroConducta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroConductaRepository extends JpaRepository<RegistroConducta, Long> {

    // Buscar registros por estudiante
    @Query("SELECT rc FROM RegistroConducta rc WHERE rc.estudiante.id = :estudianteId ORDER BY rc.fechaRegistro DESC")
    List<RegistroConducta> findByEstudianteId(@Param("estudianteId") Long estudianteId);

    // Buscar registros por docente
    @Query("SELECT rc FROM RegistroConducta rc WHERE rc.docente.id = :docenteId ORDER BY rc.fechaRegistro DESC")
    List<RegistroConducta> findByDocenteId(@Param("docenteId") Long docenteId);

    // Buscar registros por conducta
    @Query("SELECT rc FROM RegistroConducta rc WHERE rc.conducta.idConducta = :conductaId ORDER BY rc.fechaRegistro DESC")
    List<RegistroConducta> findByConductaId(@Param("conductaId") Long conductaId);

    // Buscar registros por fecha
    List<RegistroConducta> findByFechaRegistro(LocalDate fechaRegistro);

    // Buscar registros por rango de fechas
    @Query("SELECT rc FROM RegistroConducta rc WHERE rc.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY rc.fechaRegistro DESC")
    List<RegistroConducta> findByFechaRegistroBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                      @Param("fechaFin") LocalDate fechaFin);

    // Buscar registros no leídos
    List<RegistroConducta> findByLeidoFalse();

    // Buscar registros leídos
    List<RegistroConducta> findByLeidoTrue();

    // Buscar registros no leídos por estudiante
    @Query("SELECT rc FROM RegistroConducta rc WHERE rc.estudiante.id = :estudianteId AND rc.leido = false")
    List<RegistroConducta> findNoLeidosByEstudianteId(@Param("estudianteId") Long estudianteId);

    // Contar registros por estudiante
    @Query("SELECT COUNT(rc) FROM RegistroConducta rc WHERE rc.estudiante.id = :estudianteId")
    long countByEstudianteId(@Param("estudianteId") Long estudianteId);

    // Contar registros por docente
    @Query("SELECT COUNT(rc) FROM RegistroConducta rc WHERE rc.docente.id = :docenteId")
    long countByDocenteId(@Param("docenteId") Long docenteId);

    // Contar total de registros
    @Query("SELECT COUNT(rc) FROM RegistroConducta rc")
    long countRegistros();

    // Contar registros no leídos por estudiante
    @Query("SELECT COUNT(rc) FROM RegistroConducta rc WHERE rc.estudiante.id = :estudianteId AND rc.leido = false")
    long countNoLeidosByEstudianteId(@Param("estudianteId") Long estudianteId);

    // Marcar registro como leído
    @Modifying
    @Query("UPDATE RegistroConducta rc SET rc.leido = true, rc.fechaLectura = CURRENT_DATE WHERE rc.idRegistro = :id")
    void marcarComoLeido(@Param("id") Long id);

    // Obtener registros recientes (usando Spring Data JPA para el límite)
    @Query("SELECT rc FROM RegistroConducta rc ORDER BY rc.fechaRegistro DESC, rc.idRegistro DESC")
    List<RegistroConducta> findTop10ByOrderByFechaRegistroDescIdRegistroDesc();

    // Obtener registros recientes por docente (usando Spring Data JPA para el límite)
    @Query("SELECT rc FROM RegistroConducta rc WHERE rc.docente.id = :docenteId ORDER BY rc.fechaRegistro DESC, rc.idRegistro DESC")
    List<RegistroConducta> findTop5ByDocenteIdOrderByFechaRegistroDescIdRegistroDesc(@Param("docenteId") Long docenteId);

    // Obtener registros recientes por estudiante (usando Spring Data JPA para el límite)
    @Query("SELECT rc FROM RegistroConducta rc WHERE rc.estudiante.id = :estudianteId ORDER BY rc.fechaRegistro DESC, rc.idRegistro DESC")
    List<RegistroConducta> findTop5ByEstudianteIdOrderByFechaRegistroDescIdRegistroDesc(@Param("estudianteId") Long estudianteId);

    // Métodos alternativos usando native query para LIMIT
    @Query(value = "SELECT * FROM registroconductas ORDER BY fecha_registro DESC, id_registro DESC LIMIT :limit", nativeQuery = true)
    List<RegistroConducta> findRecentNative(@Param("limit") int limit);

    @Query(value = "SELECT * FROM registroconductas WHERE id_docente = :docenteId ORDER BY fecha_registro DESC, id_registro DESC LIMIT :limit", nativeQuery = true)
    List<RegistroConducta> findRecentByDocenteIdNative(@Param("docenteId") Long docenteId, @Param("limit") int limit);

    @Query(value = "SELECT * FROM registroconductas WHERE id_estudiante = :estudianteId ORDER BY fecha_registro DESC, id_registro DESC LIMIT :limit", nativeQuery = true)
    List<RegistroConducta> findRecentByEstudianteIdNative(@Param("estudianteId") Long estudianteId, @Param("limit") int limit);

    // Estadísticas de registros por gravedad
    @Query("SELECT rc.conducta.gravedad.nombreGravedad, COUNT(rc) " +
            "FROM RegistroConducta rc " +
            "GROUP BY rc.conducta.gravedad.idGravedad, rc.conducta.gravedad.nombreGravedad " +
            "ORDER BY COUNT(rc) DESC")
    List<Object[]> countByGravedad();

    // Estadísticas de registros por grado
    @Query("SELECT rc.estudiante.grado, COUNT(rc) " +
            "FROM RegistroConducta rc " +
            "GROUP BY rc.estudiante.grado " +
            "ORDER BY COUNT(rc) DESC")
    List<Object[]> countByGrado();

    // Estadísticas de registros por mes
    @Query("SELECT YEAR(rc.fechaRegistro), MONTH(rc.fechaRegistro), COUNT(rc) " +
            "FROM RegistroConducta rc " +
            "GROUP BY YEAR(rc.fechaRegistro), MONTH(rc.fechaRegistro) " +
            "ORDER BY YEAR(rc.fechaRegistro) DESC, MONTH(rc.fechaRegistro) DESC")
    List<Object[]> countByMes();

    // Buscar registros con gravedad específica
    @Query("SELECT rc FROM RegistroConducta rc WHERE rc.conducta.gravedad.idGravedad = :gravedadId")
    List<RegistroConducta> findByGravedadId(@Param("gravedadId") Long gravedadId);

    // Buscar registros por grado del estudiante
    @Query("SELECT rc FROM RegistroConducta rc WHERE rc.estudiante.grado = :grado")
    List<RegistroConducta> findByEstudianteGrado(@Param("grado") String grado);

    // Buscar registros por grado y sección del estudiante
    @Query("SELECT rc FROM RegistroConducta rc WHERE rc.estudiante.grado = :grado AND rc.estudiante.seccion = :seccion")
    List<RegistroConducta> findByEstudianteGradoAndSeccion(@Param("grado") String grado, @Param("seccion") String seccion);

    // Buscar registros por estado
    List<RegistroConducta> findByEstado(String estado);

    // Contar registros por estado
    long countByEstado(String estado);
}