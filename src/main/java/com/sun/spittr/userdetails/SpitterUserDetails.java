package com.sun.spittr.userdetails;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SpitterUserDetails extends User {
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SpitterUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public SpitterUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, long id) {
        this(username, password, authorities);
        setId(id);
    }

    @Override
    public String toString() {
        return "SpitterUserDetails{" +
                "id=" + id +
                '}';
    }
}
