package co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class FormatoAEntity_TI extends FormatoAEntity{

    private String nombreEstudiante1;
    private String nombreEstudiante2;
    private Integer codigoEstudiante1;
    private Integer codigoEstudiante2;

    public FormatoAEntity_TI(String titulo, String directorTrabajo, String objetivoGeneral,
            ArrayList<String> objetivosEspecificos) {
        super(titulo, directorTrabajo, objetivoGeneral, objetivosEspecificos);
        //TODO Auto-generated constructor stub
    }

}
