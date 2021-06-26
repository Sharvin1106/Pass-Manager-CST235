package com.example.passwordmanager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class User {
    public String email , password, phone, pincode;
    public ArrayList<String> secureCodes;

    public User(){
    }

    public User(String email , String password, String phone, ArrayList<String> secureCodes, String pincode){
        this.email= email;
        this.password = password;
        this.phone = phone;
        this.secureCodes = new ArrayList<>(secureCodes);
        this.pincode = Encrypt.encrypt(pincode);
    }
    public String getPhone() {
        return phone;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
