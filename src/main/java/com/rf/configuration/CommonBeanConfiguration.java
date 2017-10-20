package com.rf.configuration;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuración de spring, se encarga de declarar un encodeador de contraseñas fuerte
 * 
 * @author mortas
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CommonBeanConfiguration {

    @Bean
    public StrongPasswordEncryptor strongEncryptor() {
        StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
        return encryptor;
    }

}
