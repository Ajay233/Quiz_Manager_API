package com.apiTest.service;

import com.apiTest.User.model.User;
import com.apiTest.User.model.UserDTO;
import com.apiTest.config.GmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    GmailService gmailService;

    @Autowired
    GmailConfig gmailConfig;

    public void sendPermissionChangeRequestEmail(UserDTO user){
        String message = "Access permission request received from:" + "\r\n\r\n" + "name: " + user.getForename() + " " +
                user.getSurname() + "\r\n" + "Email: " + user.getEmail() + "\r\n\r\n" +
                "Request to change permission to: " + user.getPermission();
        new Thread(() -> {
            try {
                // In reality this method would need to get a list of all super users iterate through the list and
                // on each iteration, send an email to their email address
                // Or send one email to all super users if that's possible
                gmailService.sendMail(
                        "ajaymungurwork@outlook.com",
                        "Ajay",
                        "Permission change request",
                        message,
                        gmailConfig
                );
            } catch (MailSendException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendPermissionChangedEmail(User updatedUser){
        String successMessage = "Your privilege level has now been set to " + updatedUser.getPermission() +
                "\r\n\r\n\r\n" + "Regards" + "\r\n\r\n" + "The Quiz Manager App";
        new Thread(() -> {
            try{
                // Will need to change the recipient email to updatedUser.getEmail() prior to deployment to prod
                gmailService.sendMail(
                        "ajaymungurwork@outlook.com",
                        updatedUser.getForename(),
                        "RE: Permission Change Request",
                        successMessage,
                        gmailConfig
                );
            } catch (MailSendException e){
                e.printStackTrace();
            }
        }).start();
    }

}
