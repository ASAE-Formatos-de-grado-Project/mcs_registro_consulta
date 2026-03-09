package co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class FormatoADTO_Response {
    
    private Integer id; // ¡La gran diferencia! Aquí va el ID generado por tu base de datos
    private String tipoFormato; // Útil para que el front sepa si es PP o TI
    private String titulo;
    private String directorTrabajo;
    private String objetivoGeneral;
    private List<String> objetivosEspecificos;

}