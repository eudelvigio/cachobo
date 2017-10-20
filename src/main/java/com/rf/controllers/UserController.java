/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.controllers;

import com.rf.data.dto.UserDto;
import com.rf.data.entities.auth.Role;
import com.rf.data.entities.auth.User;
import com.rf.services.auth.RoleServiceImpl;
import com.rf.services.auth.UserServiceImpl;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

/**
 * Esta clase sirve para el CRUD de los usuarios de la aplicación
 * @author mortas
 */
@Controller
public class UserController {

    private UserServiceImpl userService;
    private RoleServiceImpl roleService;

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @RequestMapping(value = "/users/registration", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        User user = new User();
        model.addAttribute("user", user);

        List<Role> roles = (List<Role>) roleService.listAll();
        model.addAttribute("existingRoles", roles);
        return "users/userform";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String showUserList(WebRequest request, Model model) {
        List<User> users = (List<User>) userService.listAll();
        model.addAttribute("users", users);
        return "users/users";
    }

    @RequestMapping(value = "/user/edit/{id}", method = RequestMethod.GET)
    public String editUser(@PathVariable Integer id, WebRequest request, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);

        List<Role> roles = (List<Role>) roleService.listAll();
        model.addAttribute("existingRoles", roles);
        return "users/userform";
    }

    @RequestMapping(value = "/user/delete/{id}", method = RequestMethod.GET)
    public String delUser(@PathVariable Integer id, WebRequest request, Model model) {
        User uAux = userService.getById(id);
        uAux.getRoles().clear();
        
        List<Role> roles = (List<Role>) roleService.listAll();
        for (Role rol : roles) {
            if (rol.getUsers() != null) {
                for (Integer i = 0; i < rol.getUsers().size(); i++) {
                    if (rol.getUsers().get(i).getUsername().equals(uAux.getUsername())) {
                        rol.getUsers().remove(rol.getUsers().get(i));
                        roleService.saveOrUpdate(rol);
                    }
                }
            }
        }
        userService.saveOrUpdate(uAux);
        
        
        userService.delete(id);

        List<User> users = (List<User>) userService.listAll();
        model.addAttribute("users", users);
        return "redirect:/users";
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String list(User u) {
        User uAux = userService.findByUsername(u.getUsername());
        if (uAux == null) {
            u = userService.saveOrUpdate(u);
        } else {
            List<Role> roles = new ArrayList(new LinkedHashSet(u.getRoles())); 

            if (u.getPassword() != "") {
                uAux.setPassword(u.getPassword());
            }

            uAux.setRoles(roles);
            u = userService.saveOrUpdate(uAux);
        }
        
        return "redirect:user/edit/" + u.getId();
    }
}
