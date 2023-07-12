package com.stacktobasics.wownamechecker.profile.domain;

import lombok.NonNull;

public record Character(@NonNull String name, @NonNull String realm, @NonNull String region) {
    public Character {
        name = name.toLowerCase();
        realm = normaliseRealm(realm);
        var passedRegion = region;
        region = Regions.getIdFromName(region).orElseThrow(() -> new IllegalArgumentException(passedRegion + " is not a valid region"));
    }

    private String normaliseRealm(String realm) {
        return realm
                .replace("-", "")
                .replace(" ", "-")
                .replace("'", "")
                .toLowerCase();
    }
}
