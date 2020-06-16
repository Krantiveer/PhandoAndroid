package com.perseverance.phando.retrofit;

import java.io.Serializable;

public class RegisterCred implements Serializable {
    public String email;
    public String password;
    public String name;
    public String mobile;


    public RegisterCred(String email, String password, String firstname,String mobile) {
        this.email = email;
        this.password = password;
        this.name = firstname;
        this.mobile = mobile;
    }
}
