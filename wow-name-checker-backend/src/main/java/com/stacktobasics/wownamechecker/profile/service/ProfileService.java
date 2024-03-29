package com.stacktobasics.wownamechecker.profile.service;

import com.stacktobasics.wownamechecker.infra.clients.ProfileDTO;
import com.stacktobasics.wownamechecker.infra.clients.WoWClient;
import com.stacktobasics.wownamechecker.profile.domain.Character;
import com.stacktobasics.wownamechecker.profile.domain.Regions;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

@Service
@Slf4j
public class ProfileService {

    private final String baseURLFormat;
    private final WoWClient woWClient;


    public ProfileService(@Value("${clients.wow.url}") String baseURLFormat, WoWClient woWClient) {
        this.baseURLFormat = baseURLFormat;
        this.woWClient = woWClient;
    }

    @Cacheable("profiles")
    public Optional<ProfileDTO> getCachedProfile(String name, String realm, String region) {
        return getProfile(name, realm, region);
    }

    public Optional<ProfileDTO> getProfile(String name, String realm, String region) {
        var character = new Character(name, realm, region);
        log.info("Getting profile for name: {}, realm: {}, region: {}...", character.name(), character.realm(), character.region());
        try {
            return Optional.ofNullable(woWClient.getProfile(URI.create(String.format(baseURLFormat, character.region())), character.name(), character.realm(), "profile-" + character.region()));
        } catch (FeignException feignException) {
            if(feignException.status() == 404) return Optional.empty();
            throw feignException;
        }
    }



}
