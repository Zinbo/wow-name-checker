package com.stacktobasics.wownamechecker.infra.clients;

import com.stacktobasics.wownamechecker.infra.config.OAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

@FeignClient(
        name = "wow",
        url = "https://eu.api.blizzard.com",
        configuration = OAuthFeignConfig.class)
public interface WoWClient {

    @GetMapping("/profile/wow/character/{realm}/{name}")
    ProfileDTO getProfile(URI baseUrl, @PathVariable String name, @PathVariable String realm, @RequestParam String namespace);
}
