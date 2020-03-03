package com.apiTest.model;

public class AuthenticationResponse {

    private final String jwt;
    private User user;

    public AuthenticationResponse(User user, String jwt){
        this.user = user;
        this.jwt = jwt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getJwt() {
        return jwt;
    }

}
