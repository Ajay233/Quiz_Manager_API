package com.apiTest.authentication.model;

import com.apiTest.User.model.User;
import org.springframework.http.HttpStatus;

public class VerificationResponse {

    private User user;
    private String message;
    private HttpStatus status;

    public VerificationResponse(){};

    public VerificationResponse(User user, String message, HttpStatus status){
        this.user = user;
        this.message = message;
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
