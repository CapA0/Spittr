package com.sun.spittr.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SpitterSignupForm {
    @NotNull
    @Size(min = 2, max = 16, message = "{username.size}")
    String username;
    @NotNull
    @Size(min = 2, max = 25, message = "{password.size}")
    String password;

    public SpitterSignupForm() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SpitterSignupForm(String username, String password) {

        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "SpitterSignupForm{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
