package com.stacktobasics.wownamechecker;


import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final String baseURLFormat;

    private final WebClient webClient;

    public ProfileController(@Value("${clients.wow.url}") String baseURLFormat, WebClient webClient) {
        this.baseURLFormat = baseURLFormat;
        this.webClient = webClient;
    }

    @GetMapping
    Mono<ProfileDTO> getProfile(@NotBlank @RequestParam String name, @NotBlank @RequestParam String realm, @NotBlank @RequestParam String region) {
        var url = UriComponentsBuilder.fromHttpUrl(String.format(baseURLFormat, region))
                .path("/profile/wow/character/{realm}/{name}")
                .queryParam("namespace", "profile-{region}").build(realm, name, region);

        return this.webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(ProfileDTO.class);
    }
}
