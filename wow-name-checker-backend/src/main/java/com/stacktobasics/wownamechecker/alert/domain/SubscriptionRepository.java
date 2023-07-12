package com.stacktobasics.wownamechecker.alert.domain;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    boolean existsByEmailAndNameAndRealmAndRegion(@NotBlank String email, @NotBlank String name, @NotBlank String realm, @NotBlank String region);

    @Query("select distinct new com.stacktobasics.wownamechecker.alert.domain.RealmAndRegion(realm, region) from Subscription")
    List<RealmAndRegion> getDistinctRealmAndRegionPairs();

    List<Subscription> findByRealmAndRegion(String realm, String region);

    void deleteAllByEmail(String email);
}
