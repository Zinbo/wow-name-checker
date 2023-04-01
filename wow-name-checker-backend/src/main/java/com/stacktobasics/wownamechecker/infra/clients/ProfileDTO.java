package com.stacktobasics.wownamechecker.infra.clients;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record ProfileDTO(long id, @JsonProperty(value = "last_login_timestamp") long lastLoginTimestamp) implements Serializable {}
