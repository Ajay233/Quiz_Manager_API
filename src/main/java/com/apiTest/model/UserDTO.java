package com.apiTest.model;

import lombok.Data;

@Data
public class UserDTO extends User {

    private String newPassword;
    private String newEmail;

}
