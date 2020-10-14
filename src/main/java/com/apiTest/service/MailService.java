package com.apiTest.service;

import com.apiTest.User.model.User;
import com.apiTest.User.model.UserDTO;
import com.apiTest.authentication.model.VerificationToken;
import com.apiTest.config.GmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    GmailService gmailService;

    @Autowired
    GmailConfig gmailConfig;

    @Value("${spring.datasource.frontEndUrl}")
    private String frontEndUrl;

    public void sendWelcomeEmail(User newUser, VerificationToken verificationToken){
        // create the message that will go in the email.  will need to include the token
        String message = "In order to complete the registration process please click on the link below to verify your account:" +
                "\r\n\r\n" + frontEndUrl + "/verify?token=" + verificationToken.getToken();

        // Start a new thread so the user can be informed that their account has been successfully created
        // Send the token as a link in an email to the user
        new Thread(() -> {
            try {
                gmailService.sendMail(
                        "ajaymungurwork@outlook.com",
                        newUser.getForename(),
                        "Quiz App Account Verification",
                        message,
                        gmailConfig
                );
            } catch (MailSendException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void resendToken(User user, VerificationToken replacementToken){
        String message = "In order to complete the registration process please click on the link below to verify your account:" +
                "\r\n\r\n" + frontEndUrl + "/verify?token=" + replacementToken.getToken();
        // send email
        new Thread(() -> {
            try {
                gmailService.sendMail(
                        "ajaymungurwork@outlook.com",
                        user.getForename(),
                        "Quiz App Account Verification",
                        message,
                        gmailConfig
                );
            } catch(MailSendException e){
                e.printStackTrace();
            }
        }).start();
    }

    public void restartVerificationProcess(User user, VerificationToken token){
        String message = "Your email has just been updated, you will now need to verify this before you can resume/commence normal service." +
                "\r\n\r\n" + "Please click on the link below to verify your account:" + "\r\n\r\n" +
                frontEndUrl + "/verify?token=" + token.getToken();
        new Thread(() -> {
            try {
                gmailService.sendMail(
                        "ajaymungurwork@outlook.com",
                        user.getForename(),
                        "Quiz App Account Verification",
                        message,
                        gmailConfig
                );
            } catch(MailSendException e){
                e.printStackTrace();
            }
        }).start();
    }

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
