package com.example.proyectoappmusica;

import java.io.Serializable;

public class SessionUserData implements Serializable {

    private String userName;
    private String password;

    public SessionUserData(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
