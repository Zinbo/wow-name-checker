package com.stacktobasics.wownamechecker.alert.service;

import com.stacktobasics.wownamechecker.alert.domain.Subscription;
import com.stacktobasics.wownamechecker.alert.domain.SubscriptionRepository;
import com.stacktobasics.wownamechecker.profile.domain.Character;
import com.stacktobasics.wownamechecker.profile.service.ProfileService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertService {

    private final SubscriptionRepository subscriptionRepository;
    private final ProfileService profileService;

    public AlertService(SubscriptionRepository subscriptionRepository, ProfileService profileService) {
        this.subscriptionRepository = subscriptionRepository;
        this.profileService = profileService;
    }


    public void addAlert(@NonNull String email, @NonNull Character character) {
        email = email.toLowerCase();
        String name = character.name();
        String realm = character.realm();
        String region = character.region();
        if(subscriptionRepository.existsByEmailAndNameAndRealmAndRegion(email, name, realm, region)) {
            log.info("Subscription for email: {}, name: {}, realm: {}, region: {} already exists.", email, character, realm, region);
            return;
        }
        if(profileService.getCachedProfile(name, realm, region).isEmpty()) {
            log.info("Character with name: {}, realm: {}, and region: {} is available now!", character, realm, region);
            return;
        }

        Subscription subscription = new Subscription(email, name, realm, region);
        subscriptionRepository.save(subscription);
    }

    public void unsubscribe(@NonNull String email) {
        subscriptionRepository.deleteAllByEmail(email.toLowerCase());
    }
}
