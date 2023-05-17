package com.stacktobasics.wownamechecker.infra.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.stacktobasics.wownamechecker.WireMockConfig;
import feign.FeignException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;

import static com.stacktobasics.wownamechecker.TestHelper.*;
import static com.stacktobasics.wownamechecker.WireMockConfig.GET_PROFILE_URI_FORMAT;
import static com.stacktobasics.wownamechecker.WireMockConfig.WOW_CLIENT_BASE_URL;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WireMockConfig.class)
class WoWClientTest {

    @Autowired
    private WireMockServer wireMockServer;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WoWClient woWClient;

    @Test
    @DisplayName("getProfile with params returns profile")
    public void getProfileTest() throws JsonProcessingException {
        // arrange
        ProfileDTO expected = new ProfileDTO(1, 1234);
        String namespace = "profile-" + REGION;
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(String.format(GET_PROFILE_URI_FORMAT,REALM, CHAR_NAME, namespace)))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(expected))));

        // act
        var actual = woWClient.getProfile(URI.create(WOW_CLIENT_BASE_URL), CHAR_NAME, REALM, namespace);

        // assert
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("getProfile where profile does not exist returns 404 exception")
    public void getProfile404Test() {
        // arrange
        String namespace = "profile-" + REGION;
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(String.format(GET_PROFILE_URI_FORMAT,REALM, CHAR_NAME, namespace)))
                .willReturn(WireMock.aResponse().withStatus(404)));

        // act
        // assert
        Assertions.assertThatThrownBy(() -> woWClient.getProfile(URI.create(WOW_CLIENT_BASE_URL), CHAR_NAME, REALM, namespace))
                .isInstanceOf(FeignException.class)
                .hasFieldOrPropertyWithValue("status", 404);
    }
}