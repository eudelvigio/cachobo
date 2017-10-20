/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.data.entities.auth.base;

/**
 * Entidad para la seguridad, ver https://springframework.guru/spring-boot-web-application-part-6-spring-security-with-dao-authentication-provider/
 * @author mortas
 */
public interface DomainObject {

    Integer getId();

    void setId(Integer id);
}
