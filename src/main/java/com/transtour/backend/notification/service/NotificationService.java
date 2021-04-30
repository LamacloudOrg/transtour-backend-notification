package com.transtour.backend.notification.service;

import com.transtour.backend.notification.exception.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;


@Service
public class NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${admin.mail}") String toAdmin;

    public CompletableFuture<Void> sendMail(String message) {

        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(
                ()-> {

                    try {
                        MimeMessage msg = javaMailSender.createMimeMessage();

                        // true = multipart message
                        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
                        helper.setTo(toAdmin);
                        helper.setSubject("nuevo viaje");

                        helper.setText("<h1>"+message+"</h1>", true);

                        javaMailSender.send(msg);
                    }catch (Exception e){
                        e.printStackTrace();
                        throw new EmailException(e.getLocalizedMessage());
                    }

                }
        );

        return completableFuture;

    }

}
