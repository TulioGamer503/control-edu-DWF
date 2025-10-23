package com.controledu.repository;

import com.controledu.model.TipoGravedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad TipoGravedad.
 * Proporciona métodos CRUD y consultas personalizadas para interactuar con la tabla 'tipogravedad'.
 */
@Repository
public interface TipoGravedadRepository extends JpaRepository<TipoGravedad, Long> {

    // --- Métodos de Consulta Derivados (Spring los crea automáticamente por el nombre) ---

    /**
     * Busca un tipo de gravedad por su nombre exacto (sensible a mayúsculas/minúsculas).
     * @param nombreGravedad El nombre a buscar.
     * @return Un Optional que contiene el TipoGravedad si se encuentra.
     */
    Optional<TipoGravedad> findByNombreGravedad(String nombreGravedad);

    /**
     * Busca un tipo de gravedad por su nombre, ignorando mayúsculas y minúsculas.
     * @param nombreGravedad El nombre a buscar.
     * @return Un Optional que contiene el TipoGravedad si se encuentra.
     */
    Optional<TipoGravedad> findByNombreGravedadIgnoreCase(String nombreGravedad);

    /**
     * Verifica si ya existe un tipo de gravedad con un nombre específico.
     * Es más eficiente que hacer un find y luego comprobar si es nulo.
     * @param nombreGravedad El nombre a verificar.
     * @return true si existe, false si no.
     */
    boolean existsByNombreGravedad(String nombreGravedad);

    /**
     * Devuelve todos los tipos de gravedad ordenados alfabéticamente por nombre de forma ascendente.
     * @return Una lista de TipoGravedad ordenada.
     */
    List<TipoGravedad> findAllByOrderByNombreGravedadAsc();

    // --- Consultas JPQL Personalizadas con @Query ---

    /**
     * Busca específicamente el tipo de gravedad 'Leve' de forma optimizada.
     * @return Un Optional con el TipoGravedad 'Leve'.
     */
    @Query("SELECT tg FROM TipoGravedad tg WHERE LOWER(tg.nombreGravedad) = 'leve'")
    Optional<TipoGravedad> findLeve();

    /**
     * Busca específicamente el tipo de gravedad 'Grave' de forma optimizada.
     * @return Un Optional con el TipoGravedad 'Grave'.
     */
    @Query("SELECT tg FROM TipoGravedad tg WHERE LOWER(tg.nombreGravedad) = 'grave'")
    Optional<TipoGravedad> findGrave();

    /**
     * Busca específicamente el tipo de gravedad 'Muy Grave' de forma optimizada.
     * @return Un Optional con el TipoGravedad 'Muy Grave'.
     */
    @Query("SELECT tg FROM TipoGravedad tg WHERE LOWER(tg.nombreGravedad) = 'muy grave'")
    Optional<TipoGravedad> findMuyGrave();

    /**
     * Realiza un conteo de cuántas conductas están asociadas a cada tipo de gravedad.
     * Ideal para generar estadísticas o reportes en el dashboard.
     * @return Una lista de arreglos de Object (Object[]), donde cada arreglo contiene:
     * [String nombreGravedad, Long cantidadDeConductas].
     */
    @Query("SELECT tg.nombreGravedad, COUNT(c) " +
            "FROM TipoGravedad tg LEFT JOIN tg.conductas c " +
            "GROUP BY tg.idGravedad, tg.nombreGravedad " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> countConductasByGravedad();
}