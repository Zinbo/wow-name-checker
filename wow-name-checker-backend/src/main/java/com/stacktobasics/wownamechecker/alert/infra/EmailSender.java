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

            String emailStart = """
                    <!doctype html>
                    <html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
                        <head>
                            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                            <meta name="viewport" content="width=device-width" />
                        </head>
                        <body>
                            <h1>Hello from WoW Name Checker!</h1>
                           
                            The following names are now available:</br>
                    """;

            var emailEnd = """
                        
                        </body>
                    </html>
                    """;

            StringBuilder sb = new StringBuilder();
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
            message.setText(emailStart + sb + emailEnd);
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
