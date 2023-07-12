package com.stacktobasics.wownamechecker.profile.domain;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Optional;

public class Regions {
    private static final Map<String, String> value = Map.of("europe", "eu", "americas", "us");

    public static Optional<String> getIdFromName(@NotNull String name) {
        return Optional.ofNullable(value.get(name.toLowerCase()))
                .or(() -> value.values().stream().filter(v -> v.equalsIgnoreCase(name)).findFirst());
    }

}
