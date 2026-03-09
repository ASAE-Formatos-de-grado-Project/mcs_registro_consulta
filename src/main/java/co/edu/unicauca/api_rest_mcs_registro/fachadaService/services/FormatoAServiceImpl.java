package co.edu.unicauca.api_rest_mcs_registro.fachadaService.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models.FormatoAEntity;
import co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models.FormatoAEntity_PP;
import co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models.FormatoAEntity_TI;
import co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.repositories.FormatoARepository;
import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Request.FormatoADTO_PP_Request;
import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Request.FormatoADTO_Request;
import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Request.FormatoADTO_TI_Request;
import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Response.FormatoADTO_PP_Response;
import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Response.FormatoADTO_Response;
import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Response.FormatoADTO_TI_Response;

@Service
public class FormatoAServiceImpl implements IFormatoAService {

    private final FormatoARepository servicioAccesoBaseDatos;
    private final ModelMapper modelMapper;

    // Inyección de dependencias por constructor
    public FormatoAServiceImpl(FormatoARepository servicioAccesoBaseDatos, ModelMapper modelMapper) {
        this.servicioAccesoBaseDatos = servicioAccesoBaseDatos;
        this.modelMapper = modelMapper;
    }

    @Override
    public FormatoADTO_Response save(FormatoADTO_Request formatoRequest) {
        FormatoAEntity entity = null;

        // 1. Mapeo Polimórfico (Request DTO -> Entity)
        if (formatoRequest instanceof FormatoADTO_PP_Request) {
            entity = this.modelMapper.map(formatoRequest, FormatoAEntity_PP.class);
        } else if (formatoRequest instanceof FormatoADTO_TI_Request) {
            entity = this.modelMapper.map(formatoRequest, FormatoAEntity_TI.class);
        }

        if (entity != null) {
            // Asignamos la fecha de creación del sistema
            entity.setFechaCreacion(new Date());
            
            // 2. Guardamos en la Base de Datos
            FormatoAEntity entityGuardada = this.servicioAccesoBaseDatos.save(entity);

            // TODO: Aquí deberás publicar el mensaje en RabbitMQ para notificar al Microservicio 2
            // ej: rabbitTemplate.convertAndSend("formatos.exchange", "formato.creado", entityGuardada.getId());

            // 3. Mapeo Polimórfico inverso (Entity -> Response DTO)
            return mapearAResponse(entityGuardada);
        }
        
        return null;
    }

    @Override
    public FormatoADTO_Response findById(Integer id) {
        Optional<FormatoAEntity> optionalFormato = this.servicioAccesoBaseDatos.findById(id);
        
        if (optionalFormato.isPresent()) {
            return mapearAResponse(optionalFormato.get());
        }
        return null; // El controlador deberá manejar este null para retornar un 404 Not Found
    }

    @Override
    public List<FormatoADTO_Response> findAll(Date fechaInicio, Date fechaFin) {
        List<FormatoADTO_Response> listaRetornar = new ArrayList<>();
        
        // Asumiendo que crearás un método findAll en tu FormatoARepository
        // Optional<List<FormatoAEntity>> entidadesOpt = this.servicioAccesoBaseDatos.findAll(fechaInicio, fechaFin);
        
        /* if (entidadesOpt.isPresent()) {
            for (FormatoAEntity entity : entidadesOpt.get()) {
                listaRetornar.add(mapearAResponse(entity));
            }
        } */
        
        return listaRetornar;
    }

    /**
     * Método auxiliar privado para resolver el polimorfismo al devolver la respuesta.
     * Evita duplicar código en save, findById y findAll.
     */
    private FormatoADTO_Response mapearAResponse(FormatoAEntity entity) {
        if (entity instanceof FormatoAEntity_PP) {
            FormatoADTO_PP_Response dto = this.modelMapper.map(entity, FormatoADTO_PP_Response.class);
            dto.setTipoFormato("PP"); // Añadimos la bandera para el JSON del frontend
            return dto;
        } else if (entity instanceof FormatoAEntity_TI) {
            FormatoADTO_TI_Response dto = this.modelMapper.map(entity, FormatoADTO_TI_Response.class);
            dto.setTipoFormato("TI");
            return dto;
        }
        return null;
    }
}