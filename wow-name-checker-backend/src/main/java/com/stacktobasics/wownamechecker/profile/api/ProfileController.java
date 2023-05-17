package com.stacktobasics.wownamechecker.profile.api;


import com.stacktobasics.wownamechecker.infra.clients.ProfileDTO;
import com.stacktobasics.wownamechecker.profile.service.ProfileService;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@Slf4j
@Validated
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    ResponseEntity<ProfileDTO> getProfile(@NotEmpty @RequestParam String name, @NotEmpty @RequestParam String realm, @NotEmpty @RequestParam String region) {
        return profileService.getCachedProfile(name, realm, region).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }





}
