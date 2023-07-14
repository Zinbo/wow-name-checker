package com.stacktobasics.wownamechecker.alert.service;

import com.stacktobasics.wownamechecker.alert.domain.Subscription;
import com.stacktobasics.wownamechecker.alert.domain.SubscriptionRepository;
import com.stacktobasics.wownamechecker.infra.clients.ProfileDTO;
import com.stacktobasics.wownamechecker.profile.domain.Character;
import com.stacktobasics.wownamechecker.profile.service.ProfileService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.stacktobasics.wownamechecker.TestHelper.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    private SubscriptionRepository subscriptionRepository = Mockito.mock(SubscriptionRepository.class);
    private ProfileService profileService = Mockito.mock(ProfileService.class);
    private AlertService alertService = new AlertService(subscriptionRepository, profileService);

    @Captor
    ArgumentCaptor<Subscription> captor;

    @Test
    @DisplayName("addAlert with profile where subscription already exists does not create a sub")
    public void addAlertExistingAlertTest() {
        // arrange
        String email = "hello@email.com";
        when(subscriptionRepository.existsByEmailAndNameAndRealmAndRegion(email, CHAR_NAME, REALM, REGION))
                .thenReturn(true);
        when(profileService.getCachedProfile(CHAR_NAME, REALM, REGION)).thenReturn(Optional.of(new ProfileDTO(1, 1234)));
        var character = new Character(CHAR_NAME, REALM, REGION);
        
        // act
        alertService.addAlert(email, character);
        
        // assert
        Mockito.verify(subscriptionRepository, never()).save(Mockito.any());
    }

    @Test
    @DisplayName("addAlert with profile where name is available does nothing")
    public void addAlertAvailableProfileTest() {
        // arrange
        String email = "hello@email.com";
        when(subscriptionRepository.existsByEmailAndNameAndRealmAndRegion(email, CHAR_NAME, REALM, REGION))
                .thenReturn(false);
        when(profileService.getCachedProfile(CHAR_NAME, REALM, REGION)).thenReturn(Optional.empty());
        var character = new Character(CHAR_NAME, REALM, REGION);

        // act
        alertService.addAlert(email, character);

        // assert
        Mockito.verify(subscriptionRepository, never()).save(Mockito.any());
    }

    @Test
    @DisplayName("addAlert with unavailable profile and without existing subscription adds sub for user")
    public void addAlertTest() {
        // arrange
        String email = "hello@email.com";
        var character = new Character(CHAR_NAME, REALM, REGION);
        when(subscriptionRepository.existsByEmailAndNameAndRealmAndRegion(email, character.name(), character.realm(), character.region()))
                .thenReturn(false);
        when(profileService.getCachedProfile(character.name(), character.realm(), character.region())).thenReturn(Optional.of(new ProfileDTO(1, 1234)));
        Subscription expected = new Subscription(email, character.name(), character.realm(), character.region());

        // act
        alertService.addAlert(email, character);

        // assert
        Mockito.verify(subscriptionRepository).save(captor.capture());
        Assertions.assertThat(captor.getValue()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("unsubscribe with email calls delete on repository")
    public void unsubscribeTest() {
        // arrange
        var email = "email";

        // act
        alertService.unsubscribe(email);

        // assert
        Mockito.verify(subscriptionRepository).deleteAllByEmail(email);
    }
    
}