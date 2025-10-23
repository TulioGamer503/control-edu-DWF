package com.controledu.dto;

import com.controledu.model.RegistroConducta;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RegistroConductaResponseDTO {
    private Long idRegistro;
    private EstudianteSimpleDTO estudiante;
    private String docenteNombreCompleto;
    private ConductaSimpleDTO conducta;
    private LocalDate fechaRegistro;
    private String observaciones;
    private String estado;
    private Boolean leido;

    // Este método "mágico" convierte la entidad de la base de datos a una "foto" limpia para la API
    public static RegistroConductaResponseDTO fromEntity(RegistroConducta registro) {
        RegistroConductaResponseDTO dto = new RegistroConductaResponseDTO();
        dto.setIdRegistro(registro.getIdRegistro());
        dto.setFechaRegistro(registro.getFechaRegistro());
        dto.setObservaciones(registro.getObservaciones());
        dto.setEstado(registro.getEstado());
        dto.setLeido(registro.getLeido());

        if (registro.getEstudiante() != null) {
            EstudianteSimpleDTO estDto = new EstudianteSimpleDTO();
            estDto.setId(registro.getEstudiante().getId());
            estDto.setNombres(registro.getEstudiante().getNombres());
            estDto.setApellidos(registro.getEstudiante().getApellidos());
            estDto.setGrado(registro.getEstudiante().getGrado() + "°");
            estDto.setSeccion(registro.getEstudiante().getSeccion());
            dto.setEstudiante(estDto);
        }

        if (registro.getDocente() != null) {
            dto.setDocenteNombreCompleto(registro.getDocente().getNombres() + " " + registro.getDocente().getApellidos());
        }

        if (registro.getConducta() != null) {
            ConductaSimpleDTO conDto = new ConductaSimpleDTO();
            conDto.setNombreConducta(registro.getConducta().getNombreConducta());
            if (registro.getConducta().getGravedad() != null) {
                conDto.setGravedad(registro.getConducta().getGravedad().getNombreGravedad());
            }
            dto.setConducta(conDto);
        }

        return dto;
    }
}