/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.configuration;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para rabbitmq, genera una cola fanout por defecto para tenerla disponible
 * @author mortas
 */
@Configuration
public class RabbitConfiguration {

    @Bean
    public FanoutExchange fanout() {
        return new FanoutExchange("wanabet-instant-communications");
    }
   

}
