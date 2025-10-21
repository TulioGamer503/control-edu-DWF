package com.controledu.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tipogravedad")
public class TipoGravedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gravedad")  // Esto mapea a la columna id_gravedad en la BD
    private Long idGravedad;  // ← El campo se llama idGravedad, no id

    @Column(name = "nombre_gravedad", nullable = false, unique = true, length = 50)
    private String nombreGravedad;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "puntos")
    private Integer puntos;

    // Relación con Conducta (uno a muchos)
    @OneToMany(mappedBy = "gravedad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Conducta> conductas = new ArrayList<>();

    // Métodos utilitarios
    public boolean isLeve() {
        return "leve".equalsIgnoreCase(this.nombreGravedad);
    }

    public boolean isGrave() {
        return "grave".equalsIgnoreCase(this.nombreGravedad);
    }

    public boolean isMuyGrave() {
        return "muy grave".equalsIgnoreCase(this.nombreGravedad);
    }
}