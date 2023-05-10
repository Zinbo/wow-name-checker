package com.stacktobasics.wownamechecker.alert.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
    @DisplayName("existsByEmailAndNameAndRegionAndRealm with existing data returns true")
    public void existsByEmailAndNameAndRegionAndRealmValidDataTest() {
        // arrange
        Subscription subscription = new Subscription("hello@email.com", "Zinbaan", "eu", "Argent-Dawn");
        subscriptionRepository.save(subscription);

        // act
        boolean actual = subscriptionRepository.existsByEmailAndNameAndRegionAndRealm("hello@email.com", "Zinbaan", "eu", "Argent-Dawn");

        // assert
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("existsByEmailAndNameAndRegionAndRealm without matching data returns false")
    public void existsByEmailAndNameAndRegionAndRealmNoDataTest() {
        // arrange
        Subscription subscription = new Subscription("hello@email.com", "Zinbaan", "eu", "Argent-Dawn");
        subscriptionRepository.save(subscription);

        // act
        boolean actual = subscriptionRepository.existsByEmailAndNameAndRegionAndRealm("different@email.com", "Zinbaan", "eu", "Argent-Dawn");

        // assert
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("getDistinctRealmAndRegionPairs with multiple realms and regions gets a single copy of each pair")
    public void getDistinctRealmAndRegionPairsTest() {
        // arrange
        subscriptionRepository.saveAll(List.of(new Subscription("hello@email.com", "Zinbaan", "eu", "Argent-Dawn"),
                new Subscription("hello2@email.com", "Zinbo", "eu", "Argent-Dawn"),
                new Subscription("hello3@email.com", "OtherName", "eu", "Aman'Thul"),
                new Subscription("hello4@email.com", "OtherName2", "us", "Aman'Thul"),
                new Subscription("hello4@email.com", "OtherName3", "us", "Khadgar")));

        var expected = List.of(new RealmAndRegion( "Argent-Dawn", "eu"),
                new RealmAndRegion("Aman'Thul", "eu"),
                new RealmAndRegion("Aman'Thul", "us"),
                new RealmAndRegion("Khadgar", "us")
                );

        // act
        var actual = subscriptionRepository.getDistinctRealmAndRegionPairs();

        // assert
        Assertions.assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("findByRealmAndRegion with multiple subscriptions returns subs with that realm and region")
    public void findByRealmAndRegionTest() {
        // arrange
        List<Subscription> expected = List.of(new Subscription("hello@email.com", "Zinbaan", "eu", "Argent-Dawn"),
                new Subscription("hello2@email.com", "Zinbo", "eu", "Argent-Dawn"));
        subscriptionRepository.saveAll(expected);
        subscriptionRepository.save(new Subscription("hello3@email.com", "OtherName", "eu", "Aman'Thul"));

        // act
        List<Subscription> actual = subscriptionRepository.findByRealmAndRegion("Argent-Dawn", "eu");

        // assert
        Assertions.assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expected);
    }

}