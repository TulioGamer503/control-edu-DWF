package com.controledu.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "observaciones")
public class Observacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_observacion")
    private Long idObservacion;

    // Relación con Estudiante (muchos a uno)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    // Relación con Docente (muchos a uno)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_docente", nullable = false)
    private Docente docente;

    @Column(name = "tipo_observacion", nullable = false, length = 50)
    private String tipoObservacion;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "leido")
    private Boolean leido = false;

    @Column(name = "fecha_lectura")
    private LocalDate fechaLectura;

    // Métodos utilitarios
    public void marcarComoLeido() {
        this.leido = true;
        this.fechaLectura = LocalDate.now();
    }

    public boolean isPositiva() {
        return "positiva".equalsIgnoreCase(this.tipoObservacion);
    }

    public boolean isNegativa() {
        return "negativa".equalsIgnoreCase(this.tipoObservacion);
    }

    public boolean isNeutra() {
        return "neutra".equalsIgnoreCase(this.tipoObservacion);
    }

    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
        }
        if (this.leido == null) {
            this.leido = false;
        }
    }
}