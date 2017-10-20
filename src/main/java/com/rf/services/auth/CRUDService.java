package com.rf.services.auth;

import java.util.List;

/**
 * Obtenido de 
 * @author mortas
 */
public interface CRUDService<T> {

    List<?> listAll();

    T getById(Integer id);

    T saveOrUpdate(T domainObject);

    void delete(Integer id);

}
