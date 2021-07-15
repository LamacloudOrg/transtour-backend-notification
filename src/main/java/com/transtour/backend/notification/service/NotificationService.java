package com.transtour.backend.notification.service;

import com.transtour.backend.notification.dto.UserNotificationDTO;
import com.transtour.backend.notification.exception.EmailException;
import com.transtour.backend.notification.exception.UserNotExist;
import com.transtour.backend.notification.model.EmailNotification;
import com.transtour.backend.notification.model.UserNotification;
import com.transtour.backend.notification.repository.ENotifiactionRepository;
import com.transtour.backend.notification.repository.IUserNotifiactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Service
public class NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Qualifier(value = "EmailNotification")
    @Autowired
    ENotifiactionRepository eRpo;

    @Qualifier(value = "UserNotification")
    @Autowired
    IUserNotifiactionRepository userNotiRepo;


    public CompletableFuture<Void> sendMail(String message) {

        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(
                ()-> {

                    try {

                        List<EmailNotification> notifications = eRpo.findByActive(true);

                        MimeMessage msg = javaMailSender.createMimeMessage();

                        // true = multipart message
                        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
                        helper.setTo(notifications.get(0).getEmail());

                        notifications.remove(0);

                        notifications.stream().forEach( notification-> {
                            try {
                                helper.addCc(notification.getEmail());
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        });

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

    public CompletableFuture<String> registerToken (UserNotificationDTO userNotificationDTO){

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(
                ()->{
                    Optional<UserNotification> optionalUser = userNotiRepo.findById(userNotificationDTO.getId());
                    optionalUser.orElseThrow(UserNotExist::new);
                    UserNotification userNoti = optionalUser.get();
                    userNoti.setFcmToken(userNotificationDTO.getFcmToken());
                    //userNoti.se(userNotificationDTO.toString());
                    //userNoti.setStatus("status");
                     userNotiRepo.save(userNoti);
                    return "Se actualizo el token";
                }
        );

        return completableFuture;
    }
}
