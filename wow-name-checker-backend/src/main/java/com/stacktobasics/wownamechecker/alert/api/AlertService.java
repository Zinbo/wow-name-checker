package com.stacktobasics.wownamechecker.alert.api;

import com.stacktobasics.wownamechecker.alert.domain.Subscription;
import com.stacktobasics.wownamechecker.alert.domain.SubscriptionRepository;
import com.stacktobasics.wownamechecker.profile.api.profile.ProfileService;
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


    public void addAlert(@NonNull String email, @NonNull String character, @NonNull String realm, @NonNull String region) {
        if(subscriptionRepository.existsByEmailAndNameAndRegionAndRealm(email, character, realm, region)) {
            log.info("Subscription for email: {}, name: {}, realm: {}, region: {} already exists.", email, character, realm, region);
            return;
        }
        if(profileService.getCachedProfile(character, realm, region).isEmpty()) {
            log.info("Character with name: {}, realm: {}, and region: {} is available now!", character, realm, region);
            return;
        }

        Subscription subscription = new Subscription(email, character, region, realm);
        subscriptionRepository.save(subscription);
    }
}
