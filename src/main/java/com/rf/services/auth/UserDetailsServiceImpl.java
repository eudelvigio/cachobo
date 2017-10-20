/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.services.auth;

import com.rf.data.entities.Site;
import com.rf.data.entities.auth.User;
import com.rf.data.entities.auth.UserDetailsImpl;
import com.rf.services.ISiteService;
import com.rf.services.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author mortas
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserServiceImpl userService;
    private Converter<User, UserDetails> userUserDetailsConverter;

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Autowired
    @Qualifier(value = "userToUserDetails")
    public void setUserUserDetailsConverter(Converter<User, UserDetails> userUserDetailsConverter) {
        this.userUserDetailsConverter = userUserDetailsConverter;
    }

    private ISiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = userUserDetailsConverter.convert(userService.findByUsername(username));

        /*for (GrantedAuthority ga : userDetails.getAuthorities()) {
            if (ga.getAuthority().indexOf("SITE:") == 0) {
                Site s = siteService.getSiteByName(ga.getAuthority().replace("SITE:", ""));
                userDetails.setSite(s);
                break;
            }
        }*/
        return userDetails;
    }

}
