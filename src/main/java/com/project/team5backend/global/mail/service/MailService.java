package com.project.team5backend.global.mail.service;

import com.project.team5backend.global.mail.exception.MailErrorCode;
import com.project.team5backend.global.mail.exception.MailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}") String fromEmail;

    public void sendMail(String toMail, String code) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toMail);
            helper.setSubject("[Artie] - 이메일 인증 코드");

            String template = Files.readString(Path.of("src/main/java/com/project/team5backend/global/mail/template/code.html"), StandardCharsets.UTF_8);
            String html = template
                    .replace("{{CODE}}", code)
                            .replace("{{TTL_MINUTES}}", "5");

            helper.setText(html, true);
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
}
