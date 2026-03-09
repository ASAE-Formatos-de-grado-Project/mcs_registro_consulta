package co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models;

import java.util.ArrayList;

import lombok.Data;

@Data
public class FormatoAEntity_PP extends FormatoAEntity{

    private String nombreEstudiante;
    private Integer codigoEstudiante;
    private String asesorOrganizacion;
    private boolean tieneCartaAceptacion;
    

    public FormatoAEntity_PP(String titulo, String directorTrabajo, String objetivoGeneral,
            ArrayList<String> objetivosEspecificos) {
        super(titulo, directorTrabajo, objetivoGeneral, objetivosEspecificos);
        //TODO Auto-generated constructor stub
    }

}
