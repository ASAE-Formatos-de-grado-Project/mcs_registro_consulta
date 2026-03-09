package co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    include = JsonTypeInfo.As.PROPERTY, 
    property = "tipoFormato" // Este es el atributo discriminador en el JSON
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FormatoADTO_PP_Request.class, name = "PP"),
    @JsonSubTypes.Type(value = FormatoADTO_TI_Request.class, name = "TI")
})
public abstract class FormatoADTO_Request {
    private String titulo;
    private String directorTrabajo;
    private String objetivoGeneral;
    private List<String> objetivosEspecificos;
}