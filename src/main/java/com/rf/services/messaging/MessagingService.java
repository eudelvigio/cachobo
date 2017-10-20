package com.rf.services.messaging;

import com.rf.data.entities.messaging.Messaging;
import com.rf.data.repositories.messaging.MessagingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class MessagingService implements IMessagingService {

    @Autowired
    private MessagingRepository messagingRepository;

    @Override
    public Iterable<Messaging> listAllMessagings() {
        return messagingRepository.findAll();
    }

    @Override
    public Messaging getMessagingById(Integer id) {
        return messagingRepository.findOne(id);
    }

    @Override
    public Messaging saveMessaging(Messaging m) {
        return messagingRepository.save(m);
    }

}
