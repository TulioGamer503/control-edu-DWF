package com.controledu.service;

import com.controledu.model.TipoGravedad;
import com.controledu.repository.TipoGravedadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipoGravedadService {

    private final TipoGravedadRepository tipoGravedadRepository;

    public List<TipoGravedad> findAll() {
        return tipoGravedadRepository.findAll();
    }

    public List<TipoGravedad> findAllOrdered() {
        return tipoGravedadRepository.findAllByOrderByNombreGravedadAsc();
    }

    public Optional<TipoGravedad> findById(Long id) {
        return tipoGravedadRepository.findById(id);
    }

    public Optional<TipoGravedad> findByNombre(String nombre) {
        return tipoGravedadRepository.findByNombreGravedad(nombre);
    }

    public Optional<TipoGravedad> findByNombreIgnoreCase(String nombre) {
        return tipoGravedadRepository.findByNombreGravedadIgnoreCase(nombre);
    }

    public Optional<TipoGravedad> findLeve() {
        return tipoGravedadRepository.findLeve();
    }

    public Optional<TipoGravedad> findGrave() {
        return tipoGravedadRepository.findGrave();
    }

    public Optional<TipoGravedad> findMuyGrave() {
        return tipoGravedadRepository.findMuyGrave();
    }

    public TipoGravedad save(TipoGravedad tipoGravedad) {
        return tipoGravedadRepository.save(tipoGravedad);
    }

    public void deleteById(Long id) {
        tipoGravedadRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return tipoGravedadRepository.existsById(id);
    }

    public boolean existsByNombre(String nombre) {
        return tipoGravedadRepository.existsByNombreGravedad(nombre);
    }

    public List<Object[]> countConductasByGravedad() {
        return tipoGravedadRepository.countConductasByGravedad();
    }

    // Método para inicializar gravedades por defecto
    public void initializeDefaultGravedades() {
        if (tipoGravedadRepository.count() == 0) {
            TipoGravedad leve = new TipoGravedad();
            leve.setNombreGravedad("leve");
            leve.setDescripcion("Faltas menores que no afectan gravemente la convivencia");
            leve.setPuntos(1);
            tipoGravedadRepository.save(leve);

            TipoGravedad grave = new TipoGravedad();
            grave.setNombreGravedad("grave");
            grave.setDescripcion("Faltas que afectan significativamente la convivencia escolar");
            grave.setPuntos(3);
            tipoGravedadRepository.save(grave);

            TipoGravedad muyGrave = new TipoGravedad();
            muyGrave.setNombreGravedad("muy grave");
            muyGrave.setDescripcion("Faltas muy graves que pueden conllevar a sanciones severas");
            muyGrave.setPuntos(5);
            tipoGravedadRepository.save(muyGrave);
        }
    }
}