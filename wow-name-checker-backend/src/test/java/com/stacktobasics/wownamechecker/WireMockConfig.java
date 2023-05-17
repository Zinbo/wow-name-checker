package com.stacktobasics.wownamechecker;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {
    public static final String GET_PROFILE_URI_FORMAT = "/profile/wow/character/%s/%s?namespace=%s";
    public static final String WOW_CLIENT_BASE_URL = "http://localhost:9561";

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer server() {
        return new WireMockServer(9561);
    }

}
