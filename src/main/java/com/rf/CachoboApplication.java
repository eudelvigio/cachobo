package com.rf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Clase de inicio de spring, aquí empieza la magia
 * @author mortas
 */
@SpringBootApplication
public class CachoboApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(CachoboApplication.class, args);
    }


}
