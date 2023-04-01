package com.stacktobasics.wownamechecker.alert.domain;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    boolean existsByEmailAndNameAndRegionAndRealm(@NotBlank String email, @NotBlank String name, @NotBlank String region, @NotBlank String realm);

    @Query("select distinct new com.stacktobasics.wownamechecker.alert.domain.RealmAndRegion(realm, region) from Subscription")
    List<RealmAndRegion> getDistinctRealmAndRegionPairs();


    @Query("select distinct name from Subscription where realm = :realm and region = :region")
    List<String> findNamesByRealmAndRegion(String realm, String region);

    @Query("select email from Subscription where name = :name and  realm = :realm and region = :region")
    List<String> findEmailsByNameAndRealmAndRegion(String name, String realm, String region);

    List<Subscription> findByRealmAndRegion(String realm, String region);
}
