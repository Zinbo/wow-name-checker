package com.stacktobasics.wownamechecker.alert.infra;

import com.stacktobasics.wownamechecker.alert.domain.Subscription;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmailSender {

    private final JavaMailSender emailSender;

    public EmailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }
    public List<Result<List<UUID>>> send(List<Subscription> subsToNotify) {
        Map<String, List<Subscription>> emailToSubs = subsToNotify.stream().collect(Collectors.groupingBy(Subscription::getEmail));
        return emailToSubs.keySet().stream().map(email -> {

            StringBuilder sb = new StringBuilder();
            sb.append("Hello from WoW Name Checker!<br/>");
            sb.append("The following names are now available:<br/>");
            List<Subscription> subs = emailToSubs.get(email);
            subs.forEach(sub -> {
                sb.append("Name: ").append(sub.getName()).append("<br/>");
                sb.append("Realm: ").append(sub.getRealm()).append("<br/>");
                sb.append("Region: ").append(sub.getRegion()).append("<br/>");
                sb.append("<br/>");
            });
            sb.append("Be sure to claim them quick!");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("hello@wownamechecker.com");
            message.setTo(email);
            message.setSubject("Your subscribed names are now available in World of Warcraft!");
            message.setText(sb.toString());
            return Result.of(() -> {
                try {
                    emailSender.send(message);
                } catch(MailSendException e) {
                    throw new RuntimeException(String.format("Failed to send mail to %s for %s subs.", email, subs.size()), e);
                }
                return subs.stream().map(Subscription::getId).toList();
            });
        }).toList();
    }
}
