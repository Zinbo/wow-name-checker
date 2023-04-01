package com.stacktobasics.wownamechecker.profile.api.profile;

import java.util.Map;
import java.util.Optional;

public class Regions {
    private static final Map<String, String> value = Map.of("Europe", "eu", "Americas", "us");

    public static Optional<String> getIdFromName(String name) {
        return Optional.ofNullable(value.get(name));
    }

}
