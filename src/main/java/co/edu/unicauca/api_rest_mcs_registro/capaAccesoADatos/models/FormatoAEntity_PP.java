package co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FormatoAEntity_PP extends FormatoAEntity {

    private String nombreEstudiante;
    private Integer codigoEstudiante;
    private String asesorOrganizacion;
    private boolean tieneCartaAceptacion;
}