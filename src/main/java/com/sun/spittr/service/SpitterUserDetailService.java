package com.sun.spittr.service;

import com.sun.spittr.controller.SpittleController;
import com.sun.spittr.model.Spitter;
import com.sun.spittr.repository.SpitterRepository;
import com.sun.spittr.userdetails.SpitterUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpitterUserDetailService implements UserDetailsService {


    Logger logger = LoggerFactory.getLogger(SpitterUserDetailService.class);

    @Autowired
    private SpitterRepository spitterRepository;

    public void setSpitterRepository(SpitterRepository spitterRepository) {
        this.spitterRepository = spitterRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Spitter spitter = spitterRepository.findByUsername(username);
        if(spitter != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_SPITTER"));
            SpitterUserDetails spitterUserDetails = new SpitterUserDetails(spitter.getUsername(), spitter.getPasswordHash(), authorities, spitter.getId());

            logger.info("test, userDetails" + spitterUserDetails);

            return spitterUserDetails;
        }
        throw new UsernameNotFoundException("User '" + username + "' not found");
    }
}
