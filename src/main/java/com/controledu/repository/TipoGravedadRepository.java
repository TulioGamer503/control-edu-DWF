package com.controledu.repository;

import com.controledu.model.TipoGravedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoGravedadRepository extends JpaRepository<TipoGravedad, Long> {

    Optional<TipoGravedad> findByNombreGravedad(String nombreGravedad);
    Optional<TipoGravedad> findByNombreGravedadIgnoreCase(String nombreGravedad);
    boolean existsByNombreGravedad(String nombreGravedad);
    List<TipoGravedad> findAllByOrderByNombreGravedadAsc();

    @Query("SELECT tg FROM TipoGravedad tg WHERE LOWER(tg.nombreGravedad) = 'leve'")
    Optional<TipoGravedad> findLeve();

    @Query("SELECT tg FROM TipoGravedad tg WHERE LOWER(tg.nombreGravedad) = 'grave'")
    Optional<TipoGravedad> findGrave();

    @Query("SELECT tg FROM TipoGravedad tg WHERE LOWER(tg.nombreGravedad) = 'muy grave'")
    Optional<TipoGravedad> findMuyGrave();

    @Query("SELECT tg.nombreGravedad, COUNT(c) " +
            "FROM TipoGravedad tg LEFT JOIN tg.conductas c " +
            "GROUP BY tg.idGravedad, tg.nombreGravedad " +  // ← Cambiado de tg.id a tg.idGravedad
            "ORDER BY COUNT(c) DESC")
    List<Object[]> countConductasByGravedad();
}