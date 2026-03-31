package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.exception.ResourceNotFoundException;
import com.rynrama.simakerjabackend.model.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendSimpleMail(EmailDetails details) {

        log.info("Sending simple email started. sender={}", sender);

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            log.info("recipient={}", details.getRecipient());

            javaMailSender.send(mailMessage);

            log.info("Simple email sent successfully. sent to={}", details.getRecipient());
        } catch (Exception e) {
            log.error("Error while sending simple email. error={}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void sendMailWithAttachment(EmailDetails details) {

        log.info("Sending email with attachment started. sender={}", sender);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {

            if (details.getAttachment().isBlank()) {
                log.warn("Attachment is missing while sending email with attachment");
                throw new ResourceNotFoundException(
                        "Attachment cannot be empty"
                );
            }

            helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setText(details.getMsgBody());
            helper.setSubject(details.getSubject());

            FileSystemResource file =
                    new FileSystemResource(
                            new File(details.getAttachment())
                    );
            helper.addAttachment(
                    file.getFilename(), file
            );

            javaMailSender.send(mimeMessage);

            log.info("Email with attachment sent successfully. sent to={}", details.getRecipient());
        } catch (Exception e) {
            if (e instanceof MessagingException) {
                log.error("Error while sending email with attachment. error={}", e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }

    public void sendHtmlMail(
            EmailDetails details,
            String htmlTemplte,
            Map<String, Object> variables
    ) {

        log.info("Sending email with html content. Sender={}", sender);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {

            helper = new MimeMessageHelper(mimeMessage, true,"utf-8");

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());

            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process(htmlTemplte, context);

            helper.setText(htmlContent, true);

            if (details.getAttachment() != null && !details.getAttachment().isBlank()) {
                FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));

                if (file.exists()) {
                    helper.addAttachment(file.getFilename(), file);
                } else {
                    log.warn("Attachment file not found at path: {}", details.getAttachment());
                }
            }

            javaMailSender.send(mimeMessage);

            log.info("Email with html content sent successfully. sent to={}", details.getRecipient());
        } catch (Exception e) {
            if (e instanceof MessagingException) {
                log.error("Error sending email with html content. error={}", e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }
}
