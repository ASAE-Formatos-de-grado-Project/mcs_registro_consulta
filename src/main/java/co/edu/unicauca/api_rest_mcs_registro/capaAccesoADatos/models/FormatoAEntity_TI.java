package co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models;

import java.sql.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FormatoAEntity_TI extends FormatoAEntity {

    private String nombreEstudiante1;
    private String nombreEstudiante2;
    private Integer codigoEstudiante1;
    private Integer codigoEstudiante2;

    public FormatoAEntity_TI(Integer id, String titulo, String directorTrabajo, String objetivoGeneral,
            List<String> objetivosEspecificos, Date fechaCreacion, String nombreEstudiante1, String nombreEstudiante2, 
            Integer codigoEstudiante1, Integer codigoEstudiante2) {
        
        super(id, titulo, directorTrabajo, objetivoGeneral, objetivosEspecificos, fechaCreacion);
        
        this.nombreEstudiante1 = nombreEstudiante1;
        this.nombreEstudiante2 = nombreEstudiante2;
        this.codigoEstudiante1 = codigoEstudiante1;
        this.codigoEstudiante2 = codigoEstudiante2;
    }
}