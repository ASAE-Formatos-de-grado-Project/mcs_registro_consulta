package co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor 
public class FormatoAEntity {
    
    private Integer id; 
    private String titulo;
    private String directorTrabajo;
    private String objetivoGeneral;
    private List<String> objetivosEspecificos;
    private Date fechaCreacion;
    private String estadoActual;

}
//INVESTIGAR COMO SE HACE PARA HACER LA DESEREALIZACION DE JSON A UN PP O TI