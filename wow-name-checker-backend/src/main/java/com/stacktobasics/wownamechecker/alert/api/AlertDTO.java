package com.stacktobasics.wownamechecker.alert.api;

import jakarta.validation.constraints.NotBlank;

public record AlertDTO(@NotBlank String email, @NotBlank String character, @NotBlank String realm, @NotBlank String region) {}
