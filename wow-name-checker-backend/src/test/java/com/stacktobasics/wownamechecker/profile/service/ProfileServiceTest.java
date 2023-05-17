package com.stacktobasics.wownamechecker.profile.service;

import com.stacktobasics.wownamechecker.infra.clients.ProfileDTO;
import com.stacktobasics.wownamechecker.infra.clients.WoWClient;
import feign.FeignException;
import feign.Request;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.stacktobasics.wownamechecker.TestHelper.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProfileServiceTest {

    public static final String EXPECTED_NAME = CHAR_NAME.toLowerCase();

    public static final String NAMESPACE = "profile-" + REGION;
    private static final String EXPECTED_REALM = "argentdawn";
    public static final String URL_FORMAT = "https://%s.api.blizzard.com";
    public static final URI URL = URI.create(String.format(URL_FORMAT, REGION));
    public static final Request REQUEST = Request.create(Request.HttpMethod.GET, "url", Map.of(), Request.Body.create("body"), null);
    private final WoWClient client = mock(WoWClient.class);
    private final ProfileService profileService = Mockito.spy(new ProfileService(URL_FORMAT, client));

    @Test
    @DisplayName("getCachedProfile with values calls getProfile")
    public void getCachedProfileTest() {
        // arrange
        // act
        profileService.getCachedProfile(CHAR_NAME, REALM, REGION);

        // assert
        Mockito.verify(profileService).getProfile(CHAR_NAME, REALM, REGION);
    }

    @Test
    @DisplayName("getProfile with invalid region throws exception")
    public void getProfileInvalidRegionTest() {
        // arrange
        // act
        // assert
        Assertions.assertThatThrownBy(() -> profileService.getProfile(CHAR_NAME, REALM, "invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid is not a valid region");
    }

    public static Stream<Arguments> differentRegionsSource() {
        return Stream.of(
                Arguments.of("eu", "eu"),
                Arguments.of( "europe", "eu"),
                Arguments.of("us", "us"),
                Arguments.of("americas", "us")
        );
    }

    @ParameterizedTest
    @MethodSource("differentRegionsSource")
    @DisplayName("getProfile with different regions uses correct region id")
    public void getProfileDifferentRegionsTest(String region, String expected) {
        // arrange
        // act
        profileService.getCachedProfile(CHAR_NAME, REALM, region);

        // assert
        Mockito.verify(client).getProfile(URI.create("https://" + expected + ".api.blizzard.com"), EXPECTED_NAME, EXPECTED_REALM, "profile-" + expected);
    }

    public static Stream<Arguments> differentRealmsSource() {
        return Stream.of(
                Arguments.of("Argent-Dawn", "argentdawn"),
                Arguments.of( "Chamber of Aspects", "chamber-of-aspects"),
                Arguments.of("Aman'Thul", "amanthul"),
                Arguments.of("Saurfang", "saurfang")
        );
    }

    @ParameterizedTest
    @MethodSource("differentRealmsSource")
    @DisplayName("getProfile with different realm name types normalises realm name")
    public void getProfileDifferentRealmNamesTest(String realm, String expected) {
        // arrange
        
        // act
        profileService.getCachedProfile(CHAR_NAME, realm, REGION);
        
        // assert
        Mockito.verify(client).getProfile(URL, EXPECTED_NAME, expected, NAMESPACE);
    }
    
    @Test
    @DisplayName("getProfile with name, region, and realm returns profile")
    public void getProfileCallsClientTest() {
        // arrange
        ProfileDTO expected = new ProfileDTO(1, 12345);
        when(client.getProfile(URL, EXPECTED_NAME, EXPECTED_REALM, NAMESPACE)).thenReturn(expected);
        
        // act
        Optional<ProfileDTO> actual = profileService.getProfile(CHAR_NAME, REALM, REGION);

        // assert
        Assertions.assertThat(actual).contains(expected);
    }
    
    @Test
    @DisplayName("getProfile with bad response throws exception")
    public void getProfileBadResponseTest() {
        // arrange
        var expected = new FeignException.InternalServerError("internal server error", REQUEST, null, null);
        when(client.getProfile(URL, EXPECTED_NAME, EXPECTED_REALM, NAMESPACE))
                .thenThrow(expected);

        // act
        // assert
        Assertions.assertThatThrownBy(() -> profileService.getProfile(CHAR_NAME, REALM, REGION))
                .isEqualTo(expected);
    }
    
    @Test
    @DisplayName("getProfile with 404 response returns empty optional")
    public void getProfile404Test() {
        // arrange
        var expected = new FeignException.NotFound("internal server error", REQUEST, null, null);
        when(client.getProfile(URL, EXPECTED_NAME, EXPECTED_REALM, NAMESPACE))
                .thenThrow(expected);

        // act
        var actual = profileService.getProfile(CHAR_NAME, REALM, REGION);

        // assert
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("getProfile with null response returns empty optional")
    public void getProfileNullResponseTest() {
        // arrange
        when(client.getProfile(URL, EXPECTED_NAME, EXPECTED_REALM, NAMESPACE))
                .thenReturn(null);

        // act
        var actual = profileService.getProfile(CHAR_NAME, REALM, REGION);

        // assert
        Assertions.assertThat(actual).isEmpty();
    }
}