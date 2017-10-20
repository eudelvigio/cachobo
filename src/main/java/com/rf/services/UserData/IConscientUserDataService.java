/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.services.UserData;

import com.rf.data.entities.userdata.ConscientUserData;
import com.rf.data.enums.EnumConscientUserDataTypes;


/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IConscientUserDataService {

    Iterable<ConscientUserData> listAllConscientUserDatas();

    Iterable<ConscientUserData> listConscientUserDatasByKeyAndUserId(String key, String userId);

    Iterable<ConscientUserData> listConscientUserDatasByUserId(String userId);

    Iterable<ConscientUserData> listConscientUserDatasByUserIdImportants(String userId);

    ConscientUserData getConscientUserDataById(Integer id);

    ConscientUserData saveConscientUserData(ConscientUserData cud);

    Iterable<ConscientUserData> saveConscientUserDatas(Iterable<ConscientUserData> cuds);

    void deleteConscientUserData(ConscientUserData ud);

    Iterable<ConscientUserData> listUserDatasForReportsBySiteAndConscientUserDataType(String site, EnumConscientUserDataTypes serviceType);

    Iterable<ConscientUserData> listUserDatasForReportsBySiteAndConscientUserDataTypeAndKey(String site, EnumConscientUserDataTypes serviceType, String key);

    Iterable<ConscientUserData> listDistinctUserDatasForReportsBySiteAndConscientUserDataType(String site, EnumConscientUserDataTypes serviceType);
}
