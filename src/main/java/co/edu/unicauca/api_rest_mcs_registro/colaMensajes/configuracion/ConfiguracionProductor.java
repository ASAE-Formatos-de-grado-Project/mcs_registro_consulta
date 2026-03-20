package co.edu.unicauca.api_rest_mcs_registro.colaMensajes.configuracion;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionProductor {

    @Bean("BeanAsociadoAcolaFormatos")
    public Queue generarQueue() {
        return new Queue("colaCrearFormato", true);
    }

    @Bean("BeanAsociadoAExchangeFormatos")
    public DirectExchange generarExchangeParaFormatos() {
        return new DirectExchange("ExchangeFormatos");
    }

    @Bean
    public Binding generarbinding(@Qualifier("BeanAsociadoAcolaFormatos") Queue colaFormatos,
                                  @Qualifier("BeanAsociadoAExchangeFormatos") DirectExchange exchange) {
        return BindingBuilder.bind(colaFormatos).to(exchange).with("routingKeyFormatos");
    }

    @Bean
    public Jackson2JsonMessageConverter generarConvertidorAJSON() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter objConvertitor) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(objConvertitor);
        return rabbitTemplate;
    }

    @Bean("BeanAsociadoAcolaEstados")
    public Queue generarQueueEstados() {
        return new Queue("colaActualizarEstado", true);
    }

    @Bean("BeanAsociadoAListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter objConvertidor) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(objConvertidor);
        return factory;
    }
}