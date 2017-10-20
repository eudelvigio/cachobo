package com.rf.services.messaging;

import com.rf.data.entities.messaging.Messaging;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IMessagingService {

    Iterable<Messaging> listAllMessagings();

    Messaging getMessagingById(Integer id);

    Messaging saveMessaging(Messaging m);

}
