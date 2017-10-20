/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.services.UserData;

import com.rf.data.entities.userdata.UserData;

/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IUserDataService {

    Iterable<UserData> listAllUserDatas();

    UserData getUserDataById(Integer id);

    UserData saveUserData(UserData ud);

    Iterable<UserData> saveUsersDatas(Iterable<UserData> uds);

    void deleteUserData(UserData ud);

}
