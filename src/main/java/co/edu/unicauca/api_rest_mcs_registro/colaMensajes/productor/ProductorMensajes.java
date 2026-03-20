package co.edu.unicauca.api_rest_mcs_registro.colaMensajes.productor;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.api_rest_mcs_registro.fachadaService.DTO.Response.FormatoADTO_Response;

@Service
public class ProductorMensajes {

    @Autowired
    private RabbitTemplate amqpTemplate;

    private final String exchange = "ExchangeFormatos";
    private final String routingKey = "routingKeyFormatos";

    /**
     * Envía el formato recién creado de forma asíncrona a la cola.
     * (Fire and forget).
     */
    public void enviarFormatoCreado(FormatoADTO_Response formato) {
        System.out.println("Enviando formato ID: " + formato.getId());

        amqpTemplate.convertAndSend(exchange, routingKey, formato, message -> {
            message.getMessageProperties()
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT); //  sobrevive reinicio de RabbitMQ
            return message;
        });
    }
}