package com.apiTest.User.model;

import lombok.Data;

@Data
public class UserDTO extends User {

    private String newPassword;
    private String newEmail;

}
