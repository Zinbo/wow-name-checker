package com.stacktobasics.wownamechecker.alert.infra;

import com.stacktobasics.wownamechecker.alert.domain.RealmAndRegion;
import com.stacktobasics.wownamechecker.alert.domain.Subscription;
import com.stacktobasics.wownamechecker.alert.domain.SubscriptionRepository;
import com.stacktobasics.wownamechecker.infra.clients.ProfileDTO;
import com.stacktobasics.wownamechecker.profile.service.ProfileService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.stacktobasics.wownamechecker.TestHelper.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlertSchedulerTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private ProfileService profileService;

    @Mock
    private EmailSender emailSender;

    private AlertScheduler alertScheduler;

    @Captor
    private ArgumentCaptor<List<UUID>> idCaptor;

    @Captor
    private ArgumentCaptor<List<Subscription>> subsCaptor;

    @BeforeEach
    public void setUp() {
        alertScheduler = new AlertScheduler(subscriptionRepository, profileService, emailSender, false);
    }

    @Test
    @DisplayName("checkProfiles with multiple subs sends subs to be emailed")
    public void checkProfilesMultipleEmailsTest() {
        // arrange
        List<RealmAndRegion> realmAndRegionPairs = new ArrayList<>();
        realmAndRegionPairs.add(new RealmAndRegion(REALM, REGION));
        realmAndRegionPairs.add(new RealmAndRegion(REALM_2, REGION_2));

        when(subscriptionRepository.getDistinctRealmAndRegionPairs()).thenReturn(realmAndRegionPairs);

        var sub1 = new Subscription(EMAIL, CHAR_NAME, REALM, REGION);
        var sub2 = new Subscription(EMAIL, CHAR_NAME_2, REALM, REGION);
        var sub3 = new Subscription(EMAIL_2, CHAR_NAME_2, REALM, REGION);
        var sub4 = new Subscription(EMAIL_3, CHAR_NAME, REALM_2, REGION_2);

        var subsForFirstPair = List.of(sub1, sub2, sub3);
        var subsForSecondPair = List.of(sub4);
        when(subscriptionRepository.findByRealmAndRegion(REALM, REGION)).thenReturn(
                subsForFirstPair);
        when(subscriptionRepository.findByRealmAndRegion(REALM_2, REGION_2)).thenReturn(
                subsForSecondPair);

        when(profileService.getProfile(CHAR_NAME, REALM, REGION)).thenReturn(Optional.empty());
        when(profileService.getProfile(CHAR_NAME_2, REALM, REGION)).thenReturn(Optional.empty());
        when(profileService.getProfile(CHAR_NAME, REALM_2, REGION_2)).thenReturn(Optional.empty());

        when(emailSender.send(subsCaptor.capture())).thenReturn(List.of());

        // act
        alertScheduler.checkProfiles();
        var actualSubs = subsCaptor.getValue();

        // assert
        Assertions.assertThat(actualSubs).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(Stream.concat(subsForFirstPair.stream(), subsForSecondPair.stream()).toList());
    }

    @Test
    @DisplayName("checkProfiles with multiple subs emailed only deletes returned subs")
    public void checkProfilesDeletesSubsTest() {
        // arrange
        List<RealmAndRegion> realmAndRegionPairs = new ArrayList<>();
        realmAndRegionPairs.add(new RealmAndRegion(REALM, REGION));
        realmAndRegionPairs.add(new RealmAndRegion(REALM_2, REGION_2));

        when(subscriptionRepository.getDistinctRealmAndRegionPairs()).thenReturn(realmAndRegionPairs);

        var sub1 = new Subscription(EMAIL, CHAR_NAME, REALM, REGION);
        var sub2 = new Subscription(EMAIL, CHAR_NAME_2, REALM, REGION);
        var sub3 = new Subscription(EMAIL_2, CHAR_NAME_2, REALM, REGION);
        var sub4 = new Subscription(EMAIL_3, CHAR_NAME, REALM_2, REGION_2);

        var subsForFirstPair = List.of(sub1, sub2, sub3);
        var subsForSecondPair = List.of(sub4);
        when(subscriptionRepository.findByRealmAndRegion(REALM, REGION)).thenReturn(
                subsForFirstPair);
        when(subscriptionRepository.findByRealmAndRegion(REALM_2, REGION_2)).thenReturn(
                subsForSecondPair);

        when(profileService.getProfile(CHAR_NAME, REALM, REGION)).thenReturn(Optional.empty());
        when(profileService.getProfile(CHAR_NAME_2, REALM, REGION)).thenReturn(Optional.empty());
        when(profileService.getProfile(CHAR_NAME, REALM_2, REGION_2)).thenReturn(Optional.empty());

        List<UUID> expectedIdsForFirstPair = Stream.of(sub1, sub2).map(Subscription::getId).toList();
        List<UUID> expectedIdsForSecondPair = subsForSecondPair.stream().map(Subscription::getId).toList();
        when(emailSender.send(Mockito.any())).thenReturn(List.of(Result.ok(expectedIdsForFirstPair),
                Result.ok(expectedIdsForSecondPair)));

        var expected = Stream.concat(expectedIdsForFirstPair.stream(), expectedIdsForSecondPair.stream()).toList();

        // act
        alertScheduler.checkProfiles();

        // assert
        verify(subscriptionRepository).deleteAllById(idCaptor.capture());
        var actual = idCaptor.getValue();
        Assertions.assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("checkProfiles with 0 realms and regions deletes and sends nothing")
    public void checkProfilesNoPairsTest() {
        // arrange
        when(subscriptionRepository.getDistinctRealmAndRegionPairs()).thenReturn(List.of());

        // act
        alertScheduler.checkProfiles();

        // assert
        Mockito.verify(emailSender).send(List.of());
        Mockito.verify(subscriptionRepository).deleteAllById(List.of());
    }

    @Test
    @DisplayName("checkProfiles where profile does exist does not send email nor delete the subscription")
    public void checkProfilesWithExistingProfileTest() {
        // arrange
        List<RealmAndRegion> realmAndRegionPairs = new ArrayList<>();
        realmAndRegionPairs.add(new RealmAndRegion(REALM, REGION));
        realmAndRegionPairs.add(new RealmAndRegion(REALM_2, REGION_2));

        when(subscriptionRepository.getDistinctRealmAndRegionPairs()).thenReturn(realmAndRegionPairs);

        var sub1 = new Subscription(EMAIL, CHAR_NAME, REALM, REGION);
        var sub2 = new Subscription(EMAIL_2, CHAR_NAME, REALM, REGION);
        var sub3 = new Subscription(EMAIL, CHAR_NAME_2, REALM, REGION);
        var sub4 = new Subscription(EMAIL_3, CHAR_NAME, REALM_2, REGION_2);

        var subsForFirstPair = List.of(sub1, sub2, sub3);
        var subsForSecondPair = List.of(sub4);
        when(subscriptionRepository.findByRealmAndRegion(REALM, REGION)).thenReturn(
                subsForFirstPair);
        when(subscriptionRepository.findByRealmAndRegion(REALM_2, REGION_2)).thenReturn(
                subsForSecondPair);

        when(profileService.getProfile(CHAR_NAME, REALM, REGION)).thenReturn(Optional.of(new ProfileDTO(1, 1234)));
        when(profileService.getProfile(CHAR_NAME_2, REALM, REGION)).thenReturn(Optional.empty());
        when(profileService.getProfile(CHAR_NAME, REALM_2, REGION_2)).thenReturn(Optional.empty());

        var expectedSubs = List.of(sub3, sub4);

        List<UUID> expectedIdsForFirstPair = List.of(sub3.getId());
        List<UUID> expectedIdsForSecondPair = List.of(sub4.getId());

        when(emailSender.send(subsCaptor.capture())).thenReturn(List.of(Result.ok(expectedIdsForFirstPair),
                Result.ok(expectedIdsForSecondPair)));

        var expectedIds = Stream.concat(expectedIdsForFirstPair.stream(), expectedIdsForSecondPair.stream()).toList();

        // act
        alertScheduler.checkProfiles();

        // assert
        verify(subscriptionRepository).deleteAllById(idCaptor.capture());
        var actualIds = idCaptor.getValue();
        Assertions.assertThat(actualIds).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expectedIds);

        var actualSubs = subsCaptor.getValue();
        Assertions.assertThat(actualSubs).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expectedSubs);
    }

    @Test
    @DisplayName("checkProfiles with sub that failed to send does not delete sub id")
    public void checkProfilesWithFailuresTest() {
        // arrange
        List<RealmAndRegion> realmAndRegionPairs = new ArrayList<>();
        realmAndRegionPairs.add(new RealmAndRegion(REALM, REGION));
        realmAndRegionPairs.add(new RealmAndRegion(REALM_2, REGION_2));

        when(subscriptionRepository.getDistinctRealmAndRegionPairs()).thenReturn(realmAndRegionPairs);

        var sub1 = new Subscription(EMAIL, CHAR_NAME, REALM, REGION);
        var sub2 = new Subscription(EMAIL_2, CHAR_NAME, REALM, REGION);
        var sub3 = new Subscription(EMAIL, CHAR_NAME_2, REALM, REGION);
        var sub4 = new Subscription(EMAIL_3, CHAR_NAME, REALM_2, REGION_2);

        var subsForFirstPair = List.of(sub1, sub2, sub3);
        var subsForSecondPair = List.of(sub4);
        when(subscriptionRepository.findByRealmAndRegion(REALM, REGION)).thenReturn(
                subsForFirstPair);
        when(subscriptionRepository.findByRealmAndRegion(REALM_2, REGION_2)).thenReturn(
                subsForSecondPair);

        when(profileService.getProfile(CHAR_NAME, REALM, REGION)).thenReturn(Optional.empty());
        when(profileService.getProfile(CHAR_NAME_2, REALM, REGION)).thenReturn(Optional.empty());
        when(profileService.getProfile(CHAR_NAME, REALM_2, REGION_2)).thenReturn(Optional.empty());

        List<UUID> expectedIdsForFirstPair = List.of(sub3.getId());
        List<UUID> expectedIdsForSecondPair = List.of(sub4.getId());

        when(emailSender.send(any())).thenReturn(List.of(
                Result.error(new RuntimeException("Failed to send mail to email1@hello.com for 2 subs.")),
                Result.ok(expectedIdsForFirstPair),
                Result.ok(expectedIdsForSecondPair)));

        var expectedIds = Stream.concat(expectedIdsForFirstPair.stream(), expectedIdsForSecondPair.stream()).toList();

        // act
        alertScheduler.checkProfiles();

        // assert
        verify(subscriptionRepository).deleteAllById(idCaptor.capture());
        var actualIds = idCaptor.getValue();
        Assertions.assertThat(actualIds).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    @DisplayName("checkProfiles with ignoreChecks set to true sends email even if profile exists")
    public void checkProfilesIgnoreChecksTest() {
        // arrange
        List<RealmAndRegion> realmAndRegionPairs = new ArrayList<>();
        realmAndRegionPairs.add(new RealmAndRegion(REALM, REGION));
        realmAndRegionPairs.add(new RealmAndRegion(REALM_2, REGION_2));

        when(subscriptionRepository.getDistinctRealmAndRegionPairs()).thenReturn(realmAndRegionPairs);

        var sub1 = new Subscription(EMAIL, CHAR_NAME, REALM, REGION);
        var sub2 = new Subscription(EMAIL_2, CHAR_NAME, REALM, REGION);
        var sub3 = new Subscription(EMAIL, CHAR_NAME_2, REALM, REGION);
        var sub4 = new Subscription(EMAIL_3, CHAR_NAME, REALM_2, REGION_2);

        var subsForFirstPair = List.of(sub1, sub2, sub3);
        var subsForSecondPair = List.of(sub4);
        when(subscriptionRepository.findByRealmAndRegion(REALM, REGION)).thenReturn(
                subsForFirstPair);
        when(subscriptionRepository.findByRealmAndRegion(REALM_2, REGION_2)).thenReturn(
                subsForSecondPair);

        var expectedSubs = List.of(sub1, sub2, sub3, sub4);

        List<UUID> expectedIdsForFirstPair = List.of(sub1.getId(), sub2.getId(), sub3.getId());
        List<UUID> expectedIdsForSecondPair = List.of(sub4.getId());

        when(emailSender.send(subsCaptor.capture())).thenReturn(List.of(Result.ok(expectedIdsForFirstPair),
                Result.ok(expectedIdsForSecondPair)));

        var expectedIds = Stream.concat(expectedIdsForFirstPair.stream(), expectedIdsForSecondPair.stream()).toList();

        // act
        new AlertScheduler(subscriptionRepository, profileService, emailSender, true).checkProfiles();

        // assert
        verify(subscriptionRepository).deleteAllById(idCaptor.capture());
        var actualIds = idCaptor.getValue();
        Assertions.assertThat(actualIds).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expectedIds);

        var actualSubs = subsCaptor.getValue();
        Assertions.assertThat(actualSubs).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expectedSubs);
    }
}