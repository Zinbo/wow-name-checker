package com.stacktobasics.wownamechecker.infra.exceptionhandler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ExceptionResponseTest {

    @Test
    @DisplayName("new with params creates new ExceptionResponse")
    public void newExceptionResponseTest() {
        // arrange
        String correlationId = "123445";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "message";

        // act
        var actual = new ExceptionResponse(status, new IllegalArgumentException(message), correlationId);

        // assert
        Assertions.assertThat(actual)
                .hasFieldOrPropertyWithValue("correlationId", correlationId)
                .hasFieldOrPropertyWithValue("status", status)
                .hasFieldOrPropertyWithValue("error", message)
                .hasFieldOrProperty("datetime");
    }

}