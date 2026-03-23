package co.edu.unicauca.api_rest_mcs_registro.fachadaService.services;

import java.util.Date;
import java.util.List;

import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Request.FormatoADTO_Request;
import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Response.FormatoADTO_Response;

public interface IFormatoAService {

    List<FormatoADTO_Response> findAll();

    public FormatoADTO_Response findById(Integer id);

    public FormatoADTO_Response save(FormatoADTO_Request formato);

    List<FormatoADTO_Response> findByRangoFechas(Date fechaInicio, Date fechaFin);

}