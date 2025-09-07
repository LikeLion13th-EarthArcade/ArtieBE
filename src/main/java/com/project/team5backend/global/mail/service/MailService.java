package com.project.team5backend.global.mail.service;

import com.project.team5backend.global.mail.MailType;
import com.project.team5backend.global.mail.exception.MailErrorCode;
import com.project.team5backend.global.mail.exception.MailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}") String fromEmail;

    public void sendMail(MailType mailType, String toMail, Map<String, String> mailContent) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toMail);
            helper.setSubject(mailType.getSubject());
            String path = mailType.getTemplatePath();
            helper.setText(createTemplateWith(path, mailContent), true);
            javaMailSender.send(mimeMessage);

        } catch (IOException e) {
            throw new MailException(MailErrorCode.TEMPLATE_NOT_FOUND);
        } catch (AddressException e) {
            throw new MailException(MailErrorCode.INVALID_ADDRESS);
        } catch (MessagingException e) {
            throw new MailException(MailErrorCode.MAIL_SEND_ERROR);
        } catch (Exception e) {
            throw new MailException(MailErrorCode.NOT_DEFINED_ERROR);
        }
    }

    private String createTemplateWith(String path, Map<String, String> mailContent) throws IOException {
        String html = new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
        for (String key : mailContent.keySet()) {
            html = html.replace(key, mailContent.get(key));
        }
        return html;
    }
}
