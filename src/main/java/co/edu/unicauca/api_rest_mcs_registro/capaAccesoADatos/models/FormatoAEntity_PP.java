package co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FormatoAEntity_PP extends FormatoAEntity {

    private String nombreEstudiante;
    private Integer codigoEstudiante;
    private String asesorOrganizacion;
    private boolean tieneCartaAceptacion;

    public FormatoAEntity_PP(Integer id, String titulo, String directorTrabajo, String objetivoGeneral,
            List<String> objetivosEspecificos, Date fechaCreacion,  String nombreEstudiante, Integer codigoEstudiante, 
            String asesorOrganizacion, boolean tieneCartaAceptacion) {
        
    
        super(id, titulo, directorTrabajo, objetivoGeneral, objetivosEspecificos, fechaCreacion);
        
        this.nombreEstudiante = nombreEstudiante;
        this.codigoEstudiante = codigoEstudiante;
        this.asesorOrganizacion = asesorOrganizacion;
        this.tieneCartaAceptacion = tieneCartaAceptacion;
    }
}