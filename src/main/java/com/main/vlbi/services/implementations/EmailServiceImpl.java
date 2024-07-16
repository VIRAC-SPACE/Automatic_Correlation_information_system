package com.main.vlbi.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("emailService")
public class EmailServiceImpl {
    @Autowired
    public JavaMailSender emailSender;

    @Async
    public void send(String to, String subject, String text) {
        Date date = new Date();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("acor.parvalditajs@gmail.com");
        message.setReplyTo("acor.parvalditajs@gmail.com");
        message.setSentDate(date);

        try {
            emailSender.send(message);
        } catch (MailAuthenticationException e) {
            e.printStackTrace();
        }
    }
}
