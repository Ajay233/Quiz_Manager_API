package com.apiTest.service;

import com.apiTest.config.GmailConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class GmailService {

    public static void sendMail(String recipientEmail, String recipientForename, String msgBody, GmailConfig gmailConfig){

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", gmailConfig.getHost());
        properties.put("mail.smtp.port", gmailConfig.getPort());

        String userAccount = gmailConfig.getUsername();
        String password = gmailConfig.getPassword();

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userAccount, password);
            }
        });

        Message message = prepareMessage(session, userAccount, recipientEmail, recipientForename, msgBody);

        try {
            Transport.send(message);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    private static Message prepareMessage(Session session, String sender, String recipient, String recipientForename, String msgTxt){
        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Quiz App Account Verification");
            message.setText("Hi " + recipientForename + "\n\n" + msgTxt);
            return message;
        }catch(Exception e){
            System.out.println(e);
        }

        return null;
    }

}
