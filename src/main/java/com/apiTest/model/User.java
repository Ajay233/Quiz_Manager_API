package com.apiTest.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String forename;
    private String surname;
    private String email;
    private String password;
    private String permission;
    private String verified;

    public User(){}

    public User(String forename, String surname, String email, String password){
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.permission = "USER";
        this.verified = "false";
    }

    public long getId() {
        return id;
    }

    public String getForename(){
        return forename;
    }

    public void setForename(String forename){
        this.forename = forename;
    }

    public String getSurname(){
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermission(){
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}
