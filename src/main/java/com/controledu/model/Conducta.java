package com.controledu.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "conducta")
public class Conducta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conducta")
    private Long idConducta;

    @Column(name = "nombre_conducta", nullable = false, length = 100)
    private String nombreConducta;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    // Relación con TipoGravedad (muchos a uno)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_gravedad", nullable = false)
    private TipoGravedad gravedad;

    // Relación con RegistroConducta (uno a muchos)
    @OneToMany(mappedBy = "conducta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RegistroConducta> registrosConducta = new ArrayList<>();

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // Métodos utilitarios
    public String getNombreCompleto() {
        return this.nombreConducta + " (" + this.gravedad.getNombreGravedad() + ")";
    }
}