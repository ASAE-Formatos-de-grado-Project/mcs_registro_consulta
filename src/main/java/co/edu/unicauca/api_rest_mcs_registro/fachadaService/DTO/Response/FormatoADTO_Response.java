package co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class FormatoADTO_Response {
    
    private Integer id;
    private String tipoFormato;
    private String titulo;
    private String directorTrabajo;
    private String objetivoGeneral;
    private List<String> objetivosEspecificos;
    private String estadoActual;

}