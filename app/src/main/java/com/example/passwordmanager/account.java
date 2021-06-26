package com.example.passwordmanager;

public class account {

    String app_name;
    String user_name;
    String pass_word;
    String e_mail;
    String notes;
    String last_edited;
    String logo;

    public account() {
    }

    public account(String app_name, String user_name, String pass_word, String e_mail, String notes, String last_edited, String logo) {
        this.app_name = app_name;
        this.user_name = user_name;
        this.pass_word = pass_word;
        this.e_mail = e_mail;
        this.notes = notes;
        this.last_edited=last_edited;
        this.logo=logo;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPass_word() {
        return pass_word;
    }

    public void setPass_word(String pass_word) {
        this.pass_word = pass_word;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLast_edited() {
        return last_edited;
    }

    public void setLast_edited(String last_edited) {
        this.last_edited = last_edited;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
