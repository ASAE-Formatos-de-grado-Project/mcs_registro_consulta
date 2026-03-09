package co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FormatoADTO_TI_Response extends FormatoADTO_Response {

    private String nombreEstudiante1;
    private String nombreEstudiante2;
    private Integer codigoEstudiante1;
    private Integer codigoEstudiante2;

}