package com.rynrama.simakerjabackend.service;

import com.resend.Resend;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.rynrama.simakerjabackend.exception.ResourceNotFoundException;
import com.rynrama.simakerjabackend.model.EmailDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private Resend resend;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${resend.from.email:hello@simakerja.com}")
    private String sender;

    @Override
    public void sendSimpleMail(EmailDetails details) {
        log.info("Sending simple email started. sender={}", sender);

        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(sender)
                    .to(details.getRecipient())
                    .subject(details.getSubject())
                    .text(details.getMsgBody())
                    .build();

            log.info("recipient={}", details.getRecipient());

            CreateEmailResponse data = resend.emails().send(params);

            log.info("Simple email sent successfully via Resend. id={}, sent to={}", data.getId(), details.getRecipient());
        } catch (Exception e) {
            log.error("Error while sending simple email via Resend. error={}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMailWithAttachment(EmailDetails details) {
        log.info("Sending email with attachment started. sender={}", sender);

        try {
            if (details.getAttachment() == null || details.getAttachment().isBlank()) {
                log.warn("Attachment is missing while sending email with attachment");
                throw new ResourceNotFoundException("Attachment cannot be empty");
            }

            File file = new File(details.getAttachment());
            if (!file.exists()) {
                throw new ResourceNotFoundException("Attachment file not found at path: " + details.getAttachment());
            }

            // Resend requires attachments to be Base64 encoded
            byte[] fileContent = Files.readAllBytes(Paths.get(details.getAttachment()));
            String encodedContent = Base64.getEncoder().encodeToString(fileContent);

            Attachment attachment = Attachment.builder()
                    .fileName(file.getName())
                    .content(encodedContent)
                    .build();

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(sender)
                    .to(details.getRecipient())
                    .subject(details.getSubject())
                    .text(details.getMsgBody())
                    .attachments(Collections.singletonList(attachment))
                    .build();

            CreateEmailResponse data = resend.emails().send(params);

            log.info("Email with attachment sent successfully via Resend. id={}, sent to={}", data.getId(), details.getRecipient());
        } catch (Exception e) {
            log.error("Error while sending email with attachment via Resend. error={}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendHtmlMail(
            EmailDetails details,
            String htmlTemplte,
            Map<String, Object> variables
    ) {
        log.info("Sending email with html content. Sender={}", sender);

        try {
            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process(htmlTemplte, context);

            CreateEmailOptions.Builder paramsBuilder = CreateEmailOptions.builder()
                    .from(sender)
                    .to(details.getRecipient())
                    .subject(details.getSubject())
                    .html(htmlContent);

            if (details.getAttachment() != null && !details.getAttachment().isBlank()) {
                File file = new File(details.getAttachment());

                if (file.exists()) {
                    byte[] fileContent = Files.readAllBytes(Paths.get(details.getAttachment()));
                    String encodedContent = Base64.getEncoder().encodeToString(fileContent);

                    Attachment attachment = Attachment.builder()
                            .fileName(file.getName())
                            .content(encodedContent)
                            .build();

                    paramsBuilder.attachments(Collections.singletonList(attachment));
                } else {
                    log.warn("Attachment file not found at path: {}", details.getAttachment());
                }
            }

            CreateEmailOptions params = paramsBuilder.build();
            CreateEmailResponse data = resend.emails().send(params);

            log.info("Email with html content sent successfully via Resend. id={}, sent to={}", data.getId(), details.getRecipient());
        } catch (Exception e) {
            log.error("Error sending email with html content via Resend. error={}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
