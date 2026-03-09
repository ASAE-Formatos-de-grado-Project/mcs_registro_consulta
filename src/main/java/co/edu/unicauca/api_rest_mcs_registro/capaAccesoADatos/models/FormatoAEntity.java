package co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class FormatoAEntity {

    private String titulo;
    private String directorTrabajo;
    private String objetivoGeneral;
    private ArrayList<String> objetivosEspecificos;

}
//INVESTIGAR COMO SE HACE PARA HACER LA DESEREALIZACION DE JSON A UN PP O TI