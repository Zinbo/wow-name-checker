package com.stacktobasics.wownamechecker.alert.infra;

import com.stacktobasics.wownamechecker.alert.domain.RealmAndRegion;
import com.stacktobasics.wownamechecker.alert.domain.Subscription;
import com.stacktobasics.wownamechecker.alert.domain.SubscriptionRepository;
import com.stacktobasics.wownamechecker.profile.api.profile.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class AlertScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final ProfileService profileService;
    private final EmailSender emailSender;
    private final boolean ignoreChecks;

    public AlertScheduler(SubscriptionRepository subscriptionRepository, ProfileService profileService, EmailSender emailSender,
                          @Value("${toggles.alerts.ignore-checks}") boolean ignoreChecks) {
        this.subscriptionRepository = subscriptionRepository;
        this.profileService = profileService;
        this.emailSender = emailSender;
        this.ignoreChecks = ignoreChecks;
    }

    @Scheduled(cron = "* 0 * * * *")
    public void checkProfiles() {
        log.info("Checking if subscribed profiles still exist...");
        List<RealmAndRegion> realmAndRegionPairs = subscriptionRepository.getDistinctRealmAndRegionPairs();


        List<Subscription> subsToNotify = realmAndRegionPairs.stream().flatMap(pair -> {
            List<Subscription> subsForSameRealmAndRegion = subscriptionRepository.findByRealmAndRegion(pair.realm(), pair.region());
            var subsGroupedByName = subsForSameRealmAndRegion.stream().collect(Collectors.groupingBy(Subscription::getName));

            return subsGroupedByName.keySet().stream().flatMap(name -> {
                if(!ignoreChecks && profileService.getProfile(name, pair.realm(), pair.region()).isPresent()) return Stream.empty();
                return subsGroupedByName.get(name).stream();
            }).toList().stream();
        }).toList();

        log.info("Found {} subscriptions that require a notification", subsToNotify);

        var emailResults = emailSender.send(subsToNotify);

        removeSubsThatReceivedEmail(emailResults);

    }

    private void removeSubsThatReceivedEmail(List<Result<List<UUID>>> emailResults) {
        List<UUID> idsToRemove = new ArrayList<>();
        AtomicInteger successfulEmails = new AtomicInteger(0);
        AtomicInteger failedEmails = new AtomicInteger(0);
        emailResults.forEach(r -> r.ifOkOrElse(ids -> {
            idsToRemove.addAll(ids);
            successfulEmails.incrementAndGet();
        }, (e) -> {
            log.error(e.getMessage(), e);
            failedEmails.incrementAndGet();
        }));

        log.info("Successfully sent {} emails. Failed to send {} emails", successfulEmails, failedEmails);
        subscriptionRepository.deleteAllById(idsToRemove);
        log.info("Deleted {} subs: {}", idsToRemove.size(), idsToRemove);
    }
}
