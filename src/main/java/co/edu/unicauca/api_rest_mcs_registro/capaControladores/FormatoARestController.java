package co.edu.unicauca.api_rest_mcs_registro.capaControladores;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Request.FormatoADTO_Request;
import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Response.FormatoADTO_Response;
import co.edu.unicauca.api_rest_mcs_registro.fachadaService.services.IFormatoAService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class FormatoARestController {

    @Autowired
    private IFormatoAService formatoService;

    // 1. Consultar formatos
    @GetMapping("/formatos")
    public List<FormatoADTO_Response> listarFormatos(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {

        if (fechaInicio != null && fechaFin != null) {
            return formatoService.findByRangoFechas(fechaInicio, fechaFin);
        }
        return formatoService.findAll();
    }

    // 2. Consultar un formato A con pathvariable)
    @GetMapping("/formatos/{id}")
    public FormatoADTO_Response consultarFormato(@PathVariable Integer id) {
        FormatoADTO_Response objFormato = null;
        objFormato = formatoService.findById(id);
        return objFormato;
    }

    // 3. Registrar un formato A ya sea en modalidad PP o modalidad TI
    @PostMapping("/formatos")
    public FormatoADTO_Response crearFormato(@RequestBody FormatoADTO_Request formato) {
        FormatoADTO_Response objFormato = null;
        objFormato = formatoService.save(formato);
        return objFormato;
    }

    @GetMapping("/formatos/rango")
    public List<FormatoADTO_Response> listarPorRango(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
        return formatoService.findByRangoFechas(fechaInicio, fechaFin);
    }


}