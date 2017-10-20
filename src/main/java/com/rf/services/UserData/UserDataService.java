package com.rf.services.UserData;

import com.rf.data.entities.userdata.UserData;
import com.rf.data.repositories.UserData.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class UserDataService implements IUserDataService {

    private UserDataRepository userDataRepository;

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public Iterable<UserData> listAllUserDatas() {
        return userDataRepository.findAll();
    }

    @Override
    public UserData getUserDataById(Integer id) {
        return userDataRepository.findOne(id);
    }

    @Override
    public UserData saveUserData(UserData ud) {
        return userDataRepository.save(ud);
    }

    @Override
    public Iterable<UserData> saveUsersDatas(Iterable<UserData> uds) {
        return userDataRepository.save(uds);
    }

    @Override
    public void deleteUserData(UserData ud) {
        userDataRepository.delete(ud);
    }

}
