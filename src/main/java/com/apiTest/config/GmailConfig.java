package com.apiTest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GmailConfig {

    @Value("${spring.gmail.host}")
    private String host;

    @Value("${spring.gmail.port}")
    private String port;

    @Value("${spring.gmail.username}")
    private String username;

    @Value("${spring.gmail.password}")
    private String password;

    @Value("${spring.gmail.starttls.enable}")
    private String starttls;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
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

    public String getStarttls() {
        return starttls;
    }

    public void setStarttls(String starttls) {
        this.starttls = starttls;
    }
}
