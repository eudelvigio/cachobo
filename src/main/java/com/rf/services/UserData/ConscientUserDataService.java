package com.rf.services.UserData;

import com.rf.data.entities.userdata.ConscientUserData;
import com.rf.data.enums.EnumConscientUserDataTypes;
import com.rf.data.repositories.UserData.ConscientUserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class ConscientUserDataService implements IConscientUserDataService {

    private ConscientUserDataRepository conscientUserDataRepository;

    @Autowired
    public void setUserDataRepository(ConscientUserDataRepository conscientUserDataRepository) {
        this.conscientUserDataRepository = conscientUserDataRepository;
    }

    @Override
    public Iterable<ConscientUserData> listAllConscientUserDatas() {
        return conscientUserDataRepository.findAll();
    }

    @Override
    public ConscientUserData getConscientUserDataById(Integer id) {
        return conscientUserDataRepository.findOne(id);
    }

    @Override
    public ConscientUserData saveConscientUserData(ConscientUserData cud) {
        if (cud.getNotImportant() == null) {
            cud.setNotImportant(Boolean.FALSE);
        }
        return conscientUserDataRepository.save(cud);
    }

    @Override
    public Iterable<ConscientUserData> saveConscientUserDatas(Iterable<ConscientUserData> cuds) {
        for (ConscientUserData cud : cuds) {
            if (cud.getNotImportant() == null) {
                cud.setNotImportant(Boolean.FALSE);
            }
        }
        return conscientUserDataRepository.save(cuds);
    }

    @Override
    public void deleteConscientUserData(ConscientUserData ud) {
        conscientUserDataRepository.delete(ud);
    }

    @Override
    public Iterable<ConscientUserData> listConscientUserDatasByKeyAndUserId(String key, String userId) {
        return conscientUserDataRepository.findConscientUserDatasByKeyAndUserId(key, userId);
    }

    @Override
    public Iterable<ConscientUserData> listConscientUserDatasByUserIdImportants(String userId) {
        //return conscientUserDataRepository.findConscientUserDatasByUserId(userId);
        return conscientUserDataRepository.findConscientUserDatasByUserIdAndNotImportant(userId, false);
    }

    @Override
    public Iterable<ConscientUserData> listConscientUserDatasByUserId(String userId) {
        return conscientUserDataRepository.findConscientUserDatasByUserId(userId);
    }

    @Override
    public Iterable<ConscientUserData> listUserDatasForReportsBySiteAndConscientUserDataType(String site, EnumConscientUserDataTypes serviceType) {
        return conscientUserDataRepository.findConscientUserDatasBySiteAndConscientUserDataType(site, serviceType);
    }

    @Override
    public Iterable<ConscientUserData> listDistinctUserDatasForReportsBySiteAndConscientUserDataType(String site, EnumConscientUserDataTypes serviceType) {
        return conscientUserDataRepository.findDistinctKeyBySiteAndConscientUserDataType(site, serviceType);
    }

    @Override
    public Iterable<ConscientUserData> listUserDatasForReportsBySiteAndConscientUserDataTypeAndKey(String site, EnumConscientUserDataTypes serviceType, String key) {
        return conscientUserDataRepository.findConscientUserDatasBySiteAndConscientUserDataTypeAndKey(site, serviceType, key);
    }

}
