package co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models;

import java.util.Date;
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

}