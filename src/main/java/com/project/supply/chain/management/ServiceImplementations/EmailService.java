package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.EmailRepository;
import com.project.supply.chain.management.entity.Email;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    @Async
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("lavanyabhosale15@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        Email email = new Email();
        email.setSender("lavanyabhosale15@gmail.com");
        email.setRecipient(to);
        email.setSubject(subject);
        email.setBody(body);
        emailRepository.save(email);
    }
}
