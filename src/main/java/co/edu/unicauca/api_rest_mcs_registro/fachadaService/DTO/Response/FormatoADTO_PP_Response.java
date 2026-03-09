package co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FormatoADTO_PP_Response extends FormatoADTO_Response {

    private String nombreEstudiante;
    private Integer codigoEstudiante;
    private String asesorOrganizacion;
    private boolean tieneCartaAceptacion;

}