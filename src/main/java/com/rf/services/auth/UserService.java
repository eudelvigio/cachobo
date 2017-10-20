/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.services.auth;

import com.rf.data.entities.auth.User;

/**
 *
 * @author mortas
 */
public interface UserService extends CRUDService<User> {

    User findByUsername(String username);

}
