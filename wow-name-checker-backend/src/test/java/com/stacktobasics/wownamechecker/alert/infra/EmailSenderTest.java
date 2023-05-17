package com.stacktobasics.wownamechecker.alert.infra;

import com.stacktobasics.wownamechecker.alert.domain.Subscription;
import jakarta.mail.internet.MimeMessage;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.stacktobasics.wownamechecker.TestHelper.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderTest {

    private JavaMailSender javaMailSender = Mockito.mock(JavaMailSender.class);
    private EmailSender emailSender = new EmailSender(javaMailSender);

    @Captor
    private ArgumentCaptor<SimpleMailMessage> captor;

    @Test
    @DisplayName("send with list of subs for users sends emails to users")
    public void sendTest() throws IOException {
        // arrange
        Subscription user1Sub = new Subscription("email1@hello.com", CHAR_NAME, REALM, REGION);
        Subscription user2Sub = new Subscription("email2@hello.com", CHAR_NAME, REALM, REGION);
        Subscription user3Sub1 = new Subscription("email3@hello.com", "Noise", "Chamber of Aspects", "us");
        Subscription user3Sub2 = new Subscription("email3@hello.com", "Champ", "Chamber of Aspects", "us");
        Subscription user3Sub3 = new Subscription("email3@hello.com", "Champ", "Khadgar", "eu");
        Subscription user3Sub4 = new Subscription("email3@hello.com", "Mute", "Terrogar", "eu");
        Subscription user4Sub = new Subscription("email4@hello.com", "Jeremy", "Earthen Ring", "eu");

        var subs = List.of(user1Sub, user2Sub, user3Sub1, user3Sub2, user3Sub3, user3Sub4, user4Sub);

        var expected = List.of(generateEmailForUser(1), generateEmailForUser(2), generateEmailForUser(3), generateEmailForUser(4));

        // act
        emailSender.send(subs);
        Mockito.verify(javaMailSender, times(4)).send(captor.capture());
        var actual = captor.getAllValues();
        actual.forEach(m -> m.setText(m.getText().replace("\n", "")));

        // assert
        Assertions.assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("send with one message failing to send calls send for all emails and only returns subs of successfully sent messages")
    public void sendWithFailuresTest() {
        // arrange
        Subscription user1Sub = new Subscription("email1@hello.com", CHAR_NAME, REALM, REGION);
        Subscription user2Sub = new Subscription("email2@hello.com", CHAR_NAME, REALM, REGION);
        Subscription user3Sub1 = new Subscription("email3@hello.com", "Noise", "Chamber of Aspects", "us");
        Subscription user3Sub2 = new Subscription("email3@hello.com", "Champ", "Chamber of Aspects", "us");
        Subscription user3Sub3 = new Subscription("email3@hello.com", "Champ", "Khadgar", "eu");
        Subscription user3Sub4 = new Subscription("email3@hello.com", "Mute", "Terrogar", "eu");
        Subscription user4Sub1 = new Subscription("email4@hello.com", "Jeremy", "Earthen Ring", "eu");
        Subscription user4Sub2 = new Subscription("email4@hello.com", "Mute", "Earthen Ring", "eu");

        var subs = List.of(user1Sub, user2Sub, user3Sub1, user3Sub2, user3Sub3, user3Sub4, user4Sub1, user4Sub2);

        List<Result<List<UUID>>> expected2 = List.of(
                Result.ok(List.of(user1Sub.getId())),
                Result.ok(List.of(user2Sub.getId())),
                Result.error(new RuntimeException("Failed to send mail to email3@hello.com for 4 subs.")),
                Result.ok(List.of(user4Sub1.getId(), user4Sub2.getId())));

        doAnswer((answer) -> {
            SimpleMailMessage messageParam = answer.getArgument(0);
            var to = messageParam.getTo()[0];
            if(to.equals("email3@hello.com")) throw new MailSendException("could not send email");
            return answer;
        }).when(javaMailSender).send(Mockito.any(SimpleMailMessage.class));

        // act
        var actual = emailSender.send(subs);

        // assert
        RecursiveComparisonConfiguration rcc = new RecursiveComparisonConfiguration();
        rcc.registerComparatorForType(Comparator.comparing(Throwable::getMessage), RuntimeException.class);
        Mockito.verify(javaMailSender, times(4)).send(Mockito.any(SimpleMailMessage.class));
        Assertions.assertThat(actual).usingRecursiveFieldByFieldElementComparator(rcc)
                .containsExactlyInAnyOrderElementsOf(expected2);
    }

    @Test
    @DisplayName("send with multiple subs returns ids of all subs")
    public void sendReturnsIdsTest() {
        // arrange
        Subscription user1Sub = new Subscription("email1@hello.com", CHAR_NAME, REALM, REGION);
        Subscription user2Sub = new Subscription("email2@hello.com", CHAR_NAME, REALM, REGION);
        Subscription user3Sub1 = new Subscription("email3@hello.com", "Noise", "Chamber of Aspects", "us");
        Subscription user3Sub2 = new Subscription("email3@hello.com", "Champ", "Chamber of Aspects", "us");
        Subscription user3Sub3 = new Subscription("email3@hello.com", "Champ", "Khadgar", "eu");
        Subscription user3Sub4 = new Subscription("email3@hello.com", "Mute", "Terrogar", "eu");
        Subscription user4Sub = new Subscription("email4@hello.com", "Jeremy", "Earthen Ring", "eu");

        var subs = List.of(user1Sub, user2Sub, user3Sub1, user3Sub2, user3Sub3, user3Sub4, user4Sub);

        var expected = List.of(user1Sub.getId(), user2Sub.getId(), user3Sub1.getId(), user3Sub2.getId(),
                user3Sub3.getId(), user3Sub4.getId(), user4Sub.getId());

        // act
        var actual = emailSender.send(subs).stream().filter(Result::isOk).flatMap(r -> r.get().stream()).toList();

        // assert
        Assertions.assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expected);
    }

    private SimpleMailMessage generateEmailForUser(int user) throws IOException {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("hello@wownamechecker.com");
        email.setTo("email"+ user + "@hello.com");
        email.setSubject("Your subscribed names are now available in World of Warcraft!");
        email.setText(new String(getClass().getClassLoader().getResourceAsStream("expectedemails/user" + user + ".html").readAllBytes()));
        return email;
    }
}