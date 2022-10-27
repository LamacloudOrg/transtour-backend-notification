package com.transtour.backend.notification.service;

import com.transtour.backend.notification.dto.*;
import com.transtour.backend.notification.exception.UserNotExist;
import com.transtour.backend.notification.model.EmailNotification;
import com.transtour.backend.notification.model.UserNotification;
import com.transtour.backend.notification.repository.ENotifiactionRepository;
import com.transtour.backend.notification.repository.IUserNotifiactionRepository;
import com.transtour.backend.notification.repository.IVoucherRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
    private JavaMailSender sender;

    @Autowired
    private Configuration config;

    @Autowired
    @Qualifier("VoucherClient")
    private IVoucherRepository voucherRepository;

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    public CompletableFuture<String> registerToken(UserNotificationDTO userNotificationDTO) {

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    Optional<UserNotification> optionalUser = userNotiRepo.findById(userNotificationDTO.getId());
                    optionalUser.orElseThrow(UserNotExist::new);
                    UserNotification userNoti = optionalUser.get();
                    userNoti.setFcmToken(userNotificationDTO.getFcmToken());
                    userNoti.setDevice(userNotificationDTO.getDevice());
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

    public CompletableFuture<MailResponseDTO> sendCodeNotifcation(ActivationAccountDTO activationAccountDTO) {
        CompletableFuture<MailResponseDTO> completableFuture = CompletableFuture.supplyAsync(
                ()-> {
                    MailResponseDTO response = new MailResponseDTO();
                    MimeMessage message = sender.createMimeMessage();

                    // Llamar a la base a buscar los datos
                    EmailNotification resultEmailDriver = eRpo.findByDni(activationAccountDTO.getDriver());
                    Map<String, Object> model = new HashMap<>();

                    model.put("code", activationAccountDTO.getCode());
                    model.put("driver", resultEmailDriver.getUsername());

                    try {
                        // set mediaType
                        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                                StandardCharsets.UTF_8.name());
                        // add attachment
                        helper.addAttachment("logo.png", new ClassPathResource("logo.png"));

                        Template t = config.getTemplate("code-activation.html");
                        String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

                        helper.setTo(resultEmailDriver.getEmail());
                        helper.setText(html, true);
                        helper.setSubject("Account Activation");
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
        );

        return  completableFuture;


    }
    public CompletableFuture<String> sendPdfToPassenger(NotificationVoucherDTO notificationVoucherDTO) {

        return CompletableFuture.supplyAsync(
                () -> {

                    LOG.info("Iniciando sendPdfToPassenger Notificaciones");
                    ResponseEntity<byte[]> pdf = voucherRepository.getVoucher(notificationVoucherDTO.getTravelId());
                    //LOG.info("Que tiene pdf: " + pdf.getBody().toString());
                    try {
                        MimeMessage message = sender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message);
                        helper.setFrom("pomalianni@gmail.com");
                        helper.setTo(notificationVoucherDTO.getPassengerEmail());
                        helper.setSubject("Voucher en PDF");
                        message.setContent(multipart(pdf.getBody(), "voucher.pdf"));
                        sender.send(message);
                        LOG.info("Finalizando notificaciones");

                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                    return "Se envio el pdf por email";
                }
        );
    }


    private Multipart multipart(byte[] pdf, String filename) throws MessagingException {
        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();
// Fill the message
        messageBodyPart.setText("Voucher adjunto");
// Create a multipar message
        Multipart multipart = new MimeMultipart();
// Set text message part
        multipart.addBodyPart(messageBodyPart);
// Part two is attachment
        messageBodyPart = new MimeBodyPart();
        //String filename = "file.txt";

        DataSource source = new ByteArrayDataSource(pdf, "application/pdf");

        //DataSource source = new FileDataSource(filename);

        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);

        return multipart;
    }
}
