package com.controledu.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "registroconductas")
public class RegistroConducta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Long idRegistro;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_docente", nullable = false)
    private Docente docente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_conducta", nullable = false)
    private Conducta conducta;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "acciones_tomadas")
    private String accionesTomadas;

    @Column(name = "leido", nullable = false)
    private Boolean leido = false;

    @Column(name = "fecha_lectura")
    private LocalDate fechaLectura;

    @Column(name = "comentarios")
    private String comentarios;

    @Column(name = "evidencia_url")
    private String evidenciaUrl;

    @Column(name = "estado", nullable = false)
    private String estado = "ACTIVO";

    // ======== MÉTODOS DE CICLO DE VIDA ========

    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }
        if (this.leido == null) {
            this.leido = false;
        }
        if (this.estado == null) {
            this.estado = "ACTIVO";
        }
    }

    // ======== MÉTODO FALTANTE ========

    /**
     * Marca el registro como leído y actualiza la fecha de lectura.
     */
    public void marcarComoLeido() {
        this.leido = true;
        this.fechaLectura = LocalDate.now();
    }
}
