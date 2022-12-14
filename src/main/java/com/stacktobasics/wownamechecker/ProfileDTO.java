package com.stacktobasics.wownamechecker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProfileDTO {

    private long id;
    @JsonProperty(value = "last_login_timestamp")
    private long lastLoginTimestamp;
}
