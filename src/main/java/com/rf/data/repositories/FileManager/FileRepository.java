package com.rf.data.repositories.FileManager;

import org.springframework.data.repository.CrudRepository;

import com.rf.data.entities.filemanager.File;
import com.rf.data.entities.filemanager.Folder;

/**
 * Repositorio spring, una interfaz que extiende de crudrepository, la cual cuando sea inyectada en el servicio, 
 * dispone de los métodos CRUD básicos, además de los métodos que se definen a continuación
 * hibernate es muy listo, y a partir del nombre de la función y de los parámetros es capaz de generar las sql's necesarias el solito
 * @author mortas
 */
public interface FileRepository extends CrudRepository<File, Integer> {

    File getByNameAndParentFolder(String name, Folder folder);

    File getByName(String name);

    Iterable<File> getByParentFolder(Folder folder);
}
