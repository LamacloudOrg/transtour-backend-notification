package com.transtour.backend.notification.service;

import com.transtour.backend.notification.dto.MailRequestDTO;
import com.transtour.backend.notification.dto.MailResponseDTO;
import com.transtour.backend.notification.dto.UserNotificationDTO;
import com.transtour.backend.notification.exception.UserNotExist;
import com.transtour.backend.notification.model.EmailNotification;
import com.transtour.backend.notification.model.UserNotification;
import com.transtour.backend.notification.repository.ENotifiactionRepository;
import com.transtour.backend.notification.repository.IUserNotifiactionRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {

    @Qualifier(value = "EmailNotification")
    @Autowired
    ENotifiactionRepository eRpo;
    @Qualifier(value = "UserNotification")
    @Autowired
    IUserNotifiactionRepository userNotiRepo;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private Configuration config;

    public CompletableFuture<String> registerToken(UserNotificationDTO userNotificationDTO) {

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(
                () -> {
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

    public MailResponseDTO sendEmail(MailRequestDTO request, Map<String, Object> model) {
        MailResponseDTO response = new MailResponseDTO();
        MimeMessage message = sender.createMimeMessage();

        // Llamar a la base a buscar los datos
        EmailNotification resultEmailDriver = eRpo.findByDni(request.getDriver());
        model.put("signature", "TransTour");
        model.put("location", "Capital Federal");

        try {
            // set mediaType
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            // add attachment
            helper.addAttachment("logo.png", new ClassPathResource("logo.png"));

            Template t = config.getTemplate("emailv1.html");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

            helper.setTo(resultEmailDriver.getEmail());
            helper.setText(html, true);
            helper.setSubject("Nuevo Viaje");
            helper.setFrom("pomalianni@gmail.com");
            sender.send(message);

            response.setMessage("mail send to : " + resultEmailDriver.getEmail());
            response.setStatus(Boolean.TRUE);

        } catch (MessagingException | IOException | TemplateException e) {
            response.setMessage("Mail Sending failure : " + e.getMessage());
            response.setStatus(Boolean.FALSE);
        }

        return response;
    }
}
