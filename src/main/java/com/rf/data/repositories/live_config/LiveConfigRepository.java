package com.rf.data.repositories.live_config;

import org.springframework.data.repository.CrudRepository;

import com.rf.data.entities.Site;
import com.rf.data.entities.live_config.LiveConfig;
import org.springframework.stereotype.Repository;
/**
 * Repositorio spring, una interfaz que extiende de crudrepository, la cual cuando sea inyectada en el servicio, 
 * dispone de los métodos CRUD básicos, además de los métodos que se definen a continuación
 * hibernate es muy listo, y a partir del nombre de la función y de los parámetros es capaz de generar las sql's necesarias el solito
 * O con las anotaciones se pueden generar querys especiales
 * @author mortas
 */
@Repository
public interface LiveConfigRepository extends CrudRepository<LiveConfig, Integer> {

    Iterable<LiveConfig> findAllBySite(Site s);

    LiveConfig findBySiteAndKey(Site s, String key);
}
