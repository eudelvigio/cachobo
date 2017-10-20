package com.rf.data.repositories.UserData;

import com.rf.data.entities.userdata.ConscientUserData;
import com.rf.data.enums.EnumConscientUserDataTypes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


/**
 * Repositorio spring, una interfaz que extiende de crudrepository, la cual cuando sea inyectada en el servicio, 
 * dispone de los métodos CRUD básicos, además de los métodos que se definen a continuación
 * hibernate es muy listo, y a partir del nombre de la función y de los parámetros es capaz de generar las sql's necesarias el solito
 * O con las anotaciones se pueden generar querys especiales
 * @author mortas
 */
public interface ConscientUserDataRepository extends CrudRepository<ConscientUserData, Integer> {

    Iterable<ConscientUserData> findConscientUserDatasByKeyAndUserId(String key, String userId);

    Iterable<ConscientUserData> findConscientUserDatasByUserId(String userId);

    Iterable<ConscientUserData> findConscientUserDatasByUserIdAndNotImportant(String userId, Boolean notImportantNow);

    Iterable<ConscientUserData> findConscientUserDatasBySiteAndConscientUserDataType(String site, EnumConscientUserDataTypes serviceType);

    Iterable<ConscientUserData> findConscientUserDatasBySiteAndConscientUserDataTypeAndKey(String site, EnumConscientUserDataTypes serviceType, String key);

    @Query("SELECT DISTINCT a.key FROM ConscientUserData a WHERE a.site=?1 AND a.conscientUserDataType=?2")
    Iterable<ConscientUserData> findDistinctKeyBySiteAndConscientUserDataType(@Param("site") String site, @Param("serviceType") EnumConscientUserDataTypes serviceType);
}
