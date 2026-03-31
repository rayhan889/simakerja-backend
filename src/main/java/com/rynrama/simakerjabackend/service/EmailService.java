package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.model.EmailDetails;

import java.util.Map;

public interface EmailService {

    void sendSimpleMail(EmailDetails details);

    void sendMailWithAttachment(EmailDetails details);

    void sendHtmlMail(EmailDetails details, String htmlTemplte, Map<String, Object> variables);
}
