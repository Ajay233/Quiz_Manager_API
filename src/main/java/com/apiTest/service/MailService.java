package com.apiTest.service;

import com.apiTest.config.MailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private MailConfig mailConfig;

    private SimpleMailMessage createMessage(String message, String sender, String recipient, String subject){
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(sender);
        email.setTo(recipient);
        email.setSubject(subject);
        email.setText(message);
        return email;
    }

    private void sendEmail(SimpleMailMessage email, MailConfig mailConfig){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailConfig.getHost());
        mailSender.setPort(mailConfig.getPort());
        mailSender.setUsername(mailConfig.getUsername());
        mailSender.setPassword(mailConfig.getPassword());
        mailSender.send(email);
    }

    public void composeAndSendEmail(String message, String sender, String recipient, String subject, MailConfig config){
        SimpleMailMessage email = createMessage(message, sender, recipient, subject);
        sendEmail(email, config);
    }

}
