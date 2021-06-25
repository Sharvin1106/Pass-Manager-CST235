package com.example.passwordmanager;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class User {
    public String email , password, phone;
    public ArrayList<String> secureCodes;
    public User(){
    }

    public User(String email , String password, String phone, ArrayList<String> secureCodes){
        this.email= email;
        this.password = password;
        this.phone = phone;
        this.secureCodes = new ArrayList<>(secureCodes);
    }
    public String getPhone() {
        return phone;
    }
}
