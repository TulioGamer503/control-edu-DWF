package com.controledu.service;

import com.controledu.model.Conducta;
import com.controledu.model.TipoGravedad;
import com.controledu.repository.ConductaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConductaService {

    private final ConductaRepository conductaRepository;
    private final TipoGravedadService tipoGravedadService;

    public List<Conducta> findAll() {
        return conductaRepository.findAll();
    }

    public Optional<Conducta> findById(Long id) {
        return conductaRepository.findById(id);
    }

    public List<Conducta> findByNombreContaining(String nombre) {
        return conductaRepository.findByNombreConductaContainingIgnoreCase(nombre);
    }

    public Optional<Conducta> findByNombre(String nombre) {
        return conductaRepository.findByNombreConducta(nombre);
    }

    public List<Conducta> findByGravedad(TipoGravedad gravedad) {
        return conductaRepository.findByGravedad(gravedad);
    }

    public List<Conducta> findByGravedadId(Long gravedadId) {
        return conductaRepository.findByGravedadId(gravedadId);
    }

    public List<Conducta> findActivas() {
        return conductaRepository.findByActivoTrue();
    }

    public List<Conducta> findInactivas() {
        return conductaRepository.findByActivoFalse();
    }

    public Conducta save(Conducta conducta) {
        return conductaRepository.save(conducta);
    }

    public Conducta createConducta(String nombre, String descripcion, Long gravedadId) {
        Optional<TipoGravedad> gravedad = tipoGravedadService.findById(gravedadId);
        if (gravedad.isEmpty()) {
            throw new RuntimeException("Tipo de gravedad no encontrado");
        }

        Conducta conducta = new Conducta();
        conducta.setNombreConducta(nombre);
        conducta.setDescripcion(descripcion);
        conducta.setGravedad(gravedad.get());
        conducta.setActivo(true);

        return conductaRepository.save(conducta);
    }

    public void deleteById(Long id) {
        conductaRepository.deleteById(id);
    }

    public void desactivarConducta(Long id) {
        conductaRepository.findById(id).ifPresent(conducta -> {
            conducta.setActivo(false);
            conductaRepository.save(conducta);
        });
    }

    public void activarConducta(Long id) {
        conductaRepository.findById(id).ifPresent(conducta -> {
            conducta.setActivo(true);
            conductaRepository.save(conducta);
        });
    }

    public boolean existsById(Long id) {
        return conductaRepository.existsById(id);
    }

    public boolean existsByNombre(String nombre) {
        return conductaRepository.existsByNombreConducta(nombre);
    }

    public long count() {
        return conductaRepository.count();
    }

    public List<Object[]> countByGravedad() {
        return conductaRepository.countByGravedad();
    }

    public List<Object[]> findConductasMasUtilizadas() {
        return conductaRepository.findConductasMasUtilizadas();
    }

    public List<Conducta> findConductasNoUtilizadas() {
        return conductaRepository.findConductasNoUtilizadas();
    }
}