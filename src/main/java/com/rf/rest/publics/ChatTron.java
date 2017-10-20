package com.rf.rest.publics;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Este servicio REST controla para cada usuario el acceso a las listas de mensajes gestionadas por wanaba en un servidor rabbitmq amigo
 * @author mortas
 */
@Api(value="ChatTron", description = "Este servicio REST controla para cada usuario el acceso a las listas de mensajes gestionadas por wanaba en un servidor rabbitmq amigo")
@RestController
public class ChatTron {

    @Autowired
    private RabbitTemplate template;

    /**
     * Este método marca un mensaje entregado por el método responde como entregado, de manera que rabbitmq dejará de devolver el mensaje a futuro
     * @param deliverytag valor que tiene una referencia al mensaje
     */
    @ApiOperation(value = "Este método marca un mensaje entregado por el método responde como entregado, de manera que rabbitmq dejará de devolver el mensaje a futuro")
    @RequestMapping(value = "/messaging/delivered/{deliverytag}", method = RequestMethod.GET)
    public void enterao(@PathVariable(name = "deliverytag") long deliverytag) {
        Boolean ok = template.execute((chnl) -> {

            chnl.basicAck(deliverytag, false);

            return true;
        });
    }

    /**
     * Este método devuelve los mensajes pendientes que tiene un usuario. Los mensajes pendientes se mantendrán hasta que se llame a la función enterao de esta misma clase
     * @param usercode clave por la que se obtienen los mensajes, que suele ser el usercode de MT del usuario
     * @return lista de notificaciones pendientes.
     */
    @ApiOperation(value = "Este método devuelve los mensajes pendientes que tiene un usuario. Los mensajes pendientes se mantendrán hasta que se llame a la función enterao de esta misma clase")
    @RequestMapping(value = "/messaging/getpending/{usercode}", method = RequestMethod.GET)
    @ResponseBody
    public List<NotificationWrapper> responde(@PathVariable("usercode") String usercode) {
        List<NotificationWrapper> finalresponse = template.execute((Channel chnl) -> {
            chnl.basicRecover();
            GetResponse response = chnl.basicGet(usercode, false); //el false es para no *tener*? el ack
            List<NotificationWrapper> notificaciones = new ArrayList<>();

            while (response != null) {
                byte[] resp = null;
                resp = response.getBody();

                notificaciones.add(new NotificationWrapper(new String(resp), response.getEnvelope().getDeliveryTag()));
                response = chnl.basicGet(usercode, false);
            }
            return notificaciones;
        });

        return finalresponse;
    }

    /**
     * Pequeño wrapper para devolver los datos de las notificaciones
     */
    public static class NotificationWrapper {

        @ApiModelProperty(notes = "Mensaje a enviar")
        public final String message;
        @ApiModelProperty(notes = "id con el que marcar mensaje como entregado")
        public final long deliveryTag;

        public NotificationWrapper(String message, long deliveryTag) {
            this.message = message;
            this.deliveryTag = deliveryTag;
        }

    }

}
