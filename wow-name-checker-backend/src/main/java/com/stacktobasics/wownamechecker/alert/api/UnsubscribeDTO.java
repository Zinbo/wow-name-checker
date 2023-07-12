package com.stacktobasics.wownamechecker.alert.api;

import jakarta.validation.constraints.NotBlank;

public record UnsubscribeDTO(@NotBlank String email) {}
