/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.data.entities.auth.converters;

import com.rf.data.entities.Site;
import com.rf.data.entities.auth.User;
import com.rf.data.entities.auth.UserDetailsImpl;
import com.rf.services.SiteService;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
/**
 * Entidad para la seguridad, ver https://springframework.guru/spring-boot-web-application-part-6-spring-security-with-dao-authentication-provider/
 * @author mortas
 */
@Component
public class UserToUserDetails implements Converter<User, UserDetails> {

    private SiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    @Override
    public UserDetailsImpl convert(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl();

        if (user != null) {
            userDetails.setUsername(user.getUsername());
            userDetails.setPassword(user.getEncryptedPassword());
            userDetails.setEnabled(user.getEnabled());
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getRole()));
                if (role.getRole().indexOf("SITE:") == 0) {
                    Site s = siteService.getSiteByName(role.getRole().replace("SITE:", ""));
                    userDetails.setSite(s);
                }
            });

            userDetails.setAuthorities(authorities);
        }

        return userDetails;
    }
}
