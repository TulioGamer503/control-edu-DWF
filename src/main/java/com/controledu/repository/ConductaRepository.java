package com.controledu.repository;

import com.controledu.model.Conducta;
import com.controledu.model.TipoGravedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConductaRepository extends JpaRepository<Conducta, Long> {

    List<Conducta> findByNombreConductaContainingIgnoreCase(String nombreConducta);
    Optional<Conducta> findByNombreConducta(String nombreConducta);
    List<Conducta> findByGravedad(TipoGravedad gravedad);

    @Query("SELECT c FROM Conducta c WHERE c.gravedad.idGravedad = :idGravedad")
    List<Conducta> findByGravedadId(@Param("idGravedad") Long idGravedad);

    List<Conducta> findByActivoTrue();
    List<Conducta> findByActivoFalse();
    boolean existsByNombreConducta(String nombreConducta);

    @Query("SELECT COUNT(c) FROM Conducta c")
    long countConductas();

    @Query("SELECT c.gravedad.nombreGravedad, COUNT(c) FROM Conducta c GROUP BY c.gravedad.idGravedad, c.gravedad.nombreGravedad")
    List<Object[]> countByGravedad();

    @Query("SELECT c, COUNT(rc) as totalUsos " +
            "FROM Conducta c LEFT JOIN c.registrosConducta rc " +
            "WHERE c.activo = true " +
            "GROUP BY c.idConducta, c.nombreConducta " +
            "ORDER BY totalUsos DESC")
    List<Object[]> findConductasMasUtilizadas();

    @Query("SELECT c FROM Conducta c WHERE c.activo = true AND c.idConducta NOT IN " +
            "(SELECT rc.conducta.idConducta FROM RegistroConducta rc)")
    List<Conducta> findConductasNoUtilizadas();

    @Query("SELECT c FROM Conducta c WHERE c.descripcion LIKE %:descripcion%")
    List<Conducta> findByDescripcionContaining(@Param("descripcion") String descripcion);
}