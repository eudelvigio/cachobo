package com.rf.data.repositories.published_repositories;

import org.springframework.data.repository.CrudRepository;

import com.rf.data.entities.published_entities.CachedMedia;
/**
 * Repositorio spring, una interfaz que extiende de crudrepository, la cual cuando sea inyectada en el servicio, 
 * dispone de los métodos CRUD básicos, además de los métodos que se definen a continuación
 * hibernate es muy listo, y a partir del nombre de la función y de los parámetros es capaz de generar las sql's necesarias el solito
 * O con las anotaciones se pueden generar querys especiales
 * @author mortas
 */
public interface CachedMediaRepository extends CrudRepository<CachedMedia, Integer> {

    CachedMedia findByName(String s);

    CachedMedia findByNameIgnoreCase(String s);
}
