package co.edu.unicauca.api_rest_mcs_registro.fachadaService.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import co.edu.unicauca.api_rest_mcs_registro.colaMensajes.productor.ProductorMensajes;
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
    private final ProductorMensajes productorMensajes;

    // Inyección de dependencias por constructor
    public FormatoAServiceImpl(FormatoARepository servicioAccesoBaseDatos, ModelMapper modelMapper, ProductorMensajes productorMensajes) {
        this.servicioAccesoBaseDatos = servicioAccesoBaseDatos;
        this.modelMapper = modelMapper;
        this.productorMensajes = productorMensajes;
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
            // 2. Asignar la fecha de creación actual del sistema
            entity.setFechaCreacion(new Date());

            // 3. Persistir en la base de datos (H2)
            FormatoAEntity entityGuardada = this.servicioAccesoBaseDatos.save(entity);

            // 4. Mapeo Polimórfico inverso (Entity -> Response DTO) usando método auxiliar
            FormatoADTO_Response response = mapearAResponse(entityGuardada);

            // 5. Publicar el mensaje en la cola de RabbitMQ asíncronamente
            if (response != null && this.productorMensajes != null) {
                this.productorMensajes.enviarFormatoCreado(response);
            }

            return response;
        }

        return null;
    }

    @Override
    public FormatoADTO_Response findById(Integer id) {
        Optional<FormatoAEntity> optionalFormato = this.servicioAccesoBaseDatos.findById(id);
        
        if (optionalFormato.isPresent()) {
            return mapearAResponse(optionalFormato.get());
        }
        return null; 
    }

    @Override
    public List<FormatoADTO_Response> findAll(String tipo) {
        List<FormatoAEntity> entidades = this.servicioAccesoBaseDatos.findAll(tipo);
        List<FormatoADTO_Response> dtos = new ArrayList<>();

        for (FormatoAEntity entidad : entidades) {
            // Se reutiliza el método auxiliar para asegurar que se asigne "PP" o "TI"
            FormatoADTO_Response dto = mapearAResponse(entidad);
            if (dto != null) {
                dtos.add(dto);
            }
        }

        return dtos;
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