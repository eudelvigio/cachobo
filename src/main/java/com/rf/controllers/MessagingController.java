package com.rf.controllers;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rf.data.entities.messaging.Messaging;
import com.rf.services.messaging.MessagingService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author mortas
 */
@Controller
public class MessagingController {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private MessagingService messagingService;

    /**
     * Método que se encarga de devolver la lista de listas de mensajería :D
     * @param m Modelo que inserta spring
     * @return ruta a la template de listado
     */
    @RequestMapping(value = "/messaging")
    public String messagingList(Model m) {

        m.addAttribute("messagings", messagingService.listAllMessagings());

        return "messaging/messagings";
    }

    /**
     * Método que se encarga de añadir a modelo los datos necesarios para ir a la plantilla de creación de lista de mensajería
     * @param m Modelo que inserta spring
     * @return ruta a la template de formulario
     */
    @RequestMapping(value = "/messaging/new")
    public String messagingNew(Model m) {

        m.addAttribute("messaging", new Messaging());
        m.addAttribute("usercount", 0);

        return "messaging/messagingform";
    }

    /**
     * Método que se encarga de añadir a modelo los datos de una lista de mensajería
     * @param id Lista de mensajería a editar
     * @param m Modelo que inserta spring
     * @return ruta a la template de formulario
     */
    @RequestMapping(value = "/messaging/edit/{id}")
    public String messagingEdit(@PathVariable Integer id, Model m) {
        Messaging ms = messagingService.getMessagingById(id);

        m.addAttribute("messaging", ms);
        m.addAttribute("usercount", ms.getUsers().size());

        return "messaging/messagingform";
    }

    /**
     * Método que devuelve los datos de una lista de mensajería de cara a la generación de mensajes en cliente
     * @param id Lista de mensajería a editar
     * @param m Modelo que inserta spring
     * @return ruta a la template de envío de mensajería
     */
    @RequestMapping(value = "/messaging/{id}")
    public String messagingSend(@PathVariable Integer id, Model m) {
        Messaging ms = messagingService.getMessagingById(id);

        m.addAttribute("messaging", ms);
        m.addAttribute("usercount", ms.getUsers().size());

        return "messaging/messagingsend";
    }

    /**
     * Este método publica un mensaje en la cola correspondiente a una lista de mensajería dada
     * @param m Lista de mensajería a cuyos usuarios se enviarán los mensajes
     * @param message Mensaje a enviar
     * @return redirect a la página de envío de mensajes
     */
    @RequestMapping(value = "messagingsend", method = RequestMethod.POST)
    public String sendMessaging(Messaging m, @RequestParam("messageToSend") String message) {
        m = messagingService.getMessagingById(m.getId());
        String exchangeName = m.getExchangeName();
        template.execute((chnl) -> {

            AMQP.BasicProperties.Builder bp = new AMQP.BasicProperties.Builder()
                    .contentEncoding("UTF-8")
                    .deliveryMode(2) //persistent, 1 para non-persistent
                    //.expiration("50000")//milisegundos
                    ;
            chnl.basicPublish(exchangeName, "", bp.build(), message.getBytes());

            return true;
        });

        return "redirect:messaging/" + m.getId();
    }

    
    /**
     * Este método se encarga de crear o actualizar una lista de mensajería, creando todas las relaciones necesarias para el envío a varios usuarios
     * @param m Lista de mensajería a crear o editar
     * @return redirect al formulario de edición
     */
    @RequestMapping(value = "messaging", method = RequestMethod.POST)
    public String saveMessaging(Messaging m) {

        Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "Guardando mensajería!");

        Messaging mAux = messagingService.getMessagingById(m.getId());

        if (m.getExchangeName().isEmpty()) {
            m.setExchangeName(m.getName() + getRandomString(10));
            Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "No existia la cola, creada!");

        }
        Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "Nombre de cola [{0}]", m.getExchangeName());

        List<String> oldListaUsuarios;
        if (mAux != null) {
            oldListaUsuarios = mAux.getUsers();
            Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "Actualizando, antes existían [{0}] usuarios", oldListaUsuarios.size());
        } else {
            oldListaUsuarios = new ArrayList<>();
        }

        List<String> listaUsuarios;
        Integer numNuevosUsuarios;
        if (m.getUsers() != null) {
            listaUsuarios = m.getUsers();
            numNuevosUsuarios = listaUsuarios.size();
        } else {
            listaUsuarios = new ArrayList<>();
            numNuevosUsuarios = 0;
        }
        Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "Se van a crear [{0}] usuarios", numNuevosUsuarios);

        String exchangeName = m.getExchangeName();
        template.execute((chnl) -> {
            Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "Dentro de template");
            //Declaramos el exchange para la lista, de tipo fanout y durable
            chnl.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true); //El true para declararla durable

            Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "Creada el exchange");

            //Primero unbindamos las queues anteriores del exchange
            Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "Unbindamos los antiguos usuarios");
            for (String userIdToRemoveOfExchange : oldListaUsuarios) {

                Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "Unbindando usuario [{0}]", userIdToRemoveOfExchange);
                chnl.queueUnbind(userIdToRemoveOfExchange, exchangeName, "");
            }
            Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "Bindamos los nuevos usuarios");
            //Y para cada usuario
            for (String userId : listaUsuarios) {
                //Declaramos su queue, que no pasa ni media por repetir y repetir esto
                Logger.getLogger(MessagingController.class.getName()).log(Level.INFO, "Bindando usuario [{0}]", userId);
                chnl.queueDeclare(userId, true, false, false, null);

                //Y lo bindamos a nuestra super exchange
                chnl.queueBind(userId, exchangeName, "");
            }
            return true;
        });

        m = messagingService.saveMessaging(m);

        return "redirect:messaging/edit/" + m.getId();
    }

    /**
     * Devuelve una cadena aleatoria ASCII de {max} caracteres
     * @param max longitud de la cadena a devolver
     * @return Cadena aleatoria de {max} caracteres
     */
    private String getRandomString(Integer max) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < max; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }

}
