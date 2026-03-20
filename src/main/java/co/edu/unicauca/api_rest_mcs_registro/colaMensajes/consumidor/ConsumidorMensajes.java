package co.edu.unicauca.api_rest_mcs_registro.colaMensajes.consumidor;

import co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.repositories.FormatoARepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ConsumidorMensajes {

    private final FormatoARepository repositorio;

    public ConsumidorMensajes(FormatoARepository repositorio) {
        this.repositorio = repositorio;
    }

    @RabbitListener(queues = "colaActualizarEstado",
            containerFactory = "BeanAsociadoAListenerContainerFactory")
    public void recibirCambioEstado(Map<String, Object> mensaje) {
        Integer idFormato = (Integer) mensaje.get("idFormato");
        String nuevoEstado = (String) mensaje.get("nuevoEstado");

        System.out.println("====== CAMBIO DE ESTADO RECIBIDO EN REGISTRO ======");
        System.out.println("ID: " + idFormato + " | Nuevo estado: " + nuevoEstado);

        try {
            repositorio.actualizarEstado(idFormato, nuevoEstado);
        } catch (Exception e) {
            System.err.println("Error actualizando estado id=" + idFormato + ": " + e.getMessage());
        }
    }
}