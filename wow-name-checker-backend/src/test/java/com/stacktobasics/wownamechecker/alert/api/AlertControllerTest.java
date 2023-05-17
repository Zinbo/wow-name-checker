package com.stacktobasics.wownamechecker.alert.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacktobasics.wownamechecker.alert.infra.AlertScheduler;
import com.stacktobasics.wownamechecker.alert.service.AlertService;
import com.stacktobasics.wownamechecker.infra.clients.ProfileDTO;
import com.stacktobasics.wownamechecker.infra.config.SecurityConfig;
import com.stacktobasics.wownamechecker.infra.exceptionhandler.CorrelationIDHandler;
import com.stacktobasics.wownamechecker.infra.exceptionhandler.ExceptionResponse;
import com.stacktobasics.wownamechecker.profile.api.ProfileController;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.Stream;

import static com.stacktobasics.wownamechecker.TestHelper.*;
import static com.stacktobasics.wownamechecker.TestHelper.REALM;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertController.class)
@Import(value = {CorrelationIDHandler.class, SecurityConfig.class})
class AlertControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlertService alertService;
    @MockBean
    private AlertScheduler alertScheduler;

    public static Stream<Arguments> badRequestSource() {
        return Stream.of(
                Arguments.of(new AlertDTO("", CHAR_NAME, REALM, REGION), "email"),
                Arguments.of(new AlertDTO(EMAIL, "", REALM, REGION), "character"),
                Arguments.of(new AlertDTO(EMAIL, CHAR_NAME, "", REGION), "realm"),
                Arguments.of(new AlertDTO(EMAIL, CHAR_NAME, REALM, ""), "region"));
    }

    @ParameterizedTest
    @MethodSource("badRequestSource")
    @DisplayName("addAlert with bad request returns 400")
    public void addAlertBadRequestTest(AlertDTO alertDTO, String invalidFieldName) throws Exception {
        // arrange
        var expectedMessage = "method 'addAlert' parameter 0 failed validation with 1 errors.\n" +
                "Errors: [\n" +
                "Field: \"" + invalidFieldName + "\", Message: \"must not be blank\"\n" +
                "]";
        // act
        var result = mockMvc.perform(post("/alert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alertDTO)))
                .andExpect(status().isBadRequest())
                .andReturn();

        var node = objectMapper.readTree(result.getResponse().getContentAsString());
        var actual = node.get("error").asText();

        // assert
        Assertions.assertThat(actual).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("addAlert with valid alertDTO calls alertService method")
    public void addAlertValidTest() throws Exception {
        // arrange
        var alert = new AlertDTO(EMAIL, CHAR_NAME, REALM, REGION);

        // act
        mockMvc.perform(post("/alert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alert)))
                .andExpect(status().isOk());

        // assert
        Mockito.verify(alertService).addAlert(EMAIL, CHAR_NAME, REALM, REGION);
    }

    @Test
    @DisplayName("scheduler with call calls alertScheduler method")
    public void schedulerTest() throws Exception {
        // arrange

        // act
        mockMvc.perform(post("/alert/trigger-scheduler"))
                .andExpect(status().isOk());

        // assert
        Mockito.verify(alertScheduler).checkProfiles();
    }

}