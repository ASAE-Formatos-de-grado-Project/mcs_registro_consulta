package co.edu.unicauca.api_rest_mcs_registro.fachadaService.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class mapper {
    
    @Bean 
    public ModelMapper crearMapper() {
        ModelMapper objMapeador= new ModelMapper();
        return objMapeador;
    }
}