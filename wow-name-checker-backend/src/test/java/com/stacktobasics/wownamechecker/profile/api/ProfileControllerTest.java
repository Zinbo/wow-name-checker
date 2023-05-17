package com.stacktobasics.wownamechecker.profile.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacktobasics.wownamechecker.infra.clients.ProfileDTO;
import com.stacktobasics.wownamechecker.infra.config.SecurityConfig;
import com.stacktobasics.wownamechecker.infra.exceptionhandler.CorrelationIDHandler;
import com.stacktobasics.wownamechecker.profile.service.ProfileService;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.stream.Stream;

import static com.stacktobasics.wownamechecker.TestHelper.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProfileController.class)
@Import(value = {CorrelationIDHandler.class, SecurityConfig.class})
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileService profileService;

    public static Stream<Arguments> differentRegionsSource() {
        return Stream.of(
                Arguments.of("", REALM, REGION, "name"),
                Arguments.of(CHAR_NAME, "", REGION, "realm"),
                Arguments.of(CHAR_NAME, REALM, "", "region")
        );
    }

    @ParameterizedTest
    @MethodSource("differentRegionsSource")
    @DisplayName("/profile with blank variables returns bad request")
    public void profileWithBlankTest(String name, String realm, String region, String missingParamName) throws Exception {
        // arrange
        // act
        // assert
        mockMvc.perform(get("/profile").param("name", name).param("realm", realm).param("region", region))
                .andExpectAll(status().isBadRequest(), content().string(Matchers.containsString("getProfile." + missingParamName + ": must not be empty")));

    }

    @Test
    @DisplayName("/profile with existing profile returns 200")
    public void profileExistsTest() throws Exception {
        // arrange
        ProfileDTO expected = new ProfileDTO(1, 2);
        when(profileService.getCachedProfile(CHAR_NAME, REALM, REGION)).thenReturn(Optional.of(expected));

        // act
        MvcResult mvcResult = mockMvc.perform(get("/profile").param("name", CHAR_NAME).param("realm", REALM).param("region", REGION))
                .andExpect(status().isOk())
                .andReturn();
        var actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProfileDTO.class);

        // assert
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("/profile with profile that doesn't exist returns 404")
    public void profileDoesNotExistTest() throws Exception {
        // arrange
        when(profileService.getCachedProfile(CHAR_NAME, REALM, REGION)).thenReturn(Optional.empty());

        // act
        // assert
        mockMvc.perform(get("/profile").param("name", CHAR_NAME).param("realm", REALM).param("region", REGION))
                .andExpect(status().isNotFound());

        Mockito.verify(profileService).getCachedProfile(CHAR_NAME, REALM, REGION);
    }

}