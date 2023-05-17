package com.stacktobasics.wownamechecker.infra.exceptionhandler;

import io.micrometer.tracing.Tracer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CorrelationIDHandlerTest {

    private final Tracer tracer = mock(Tracer.class, RETURNS_DEEP_STUBS);
    private final CorrelationIDHandler correlationIDHandler = new CorrelationIDHandler(tracer);

    @Test
    @DisplayName("getCorrelationId with tracer gets trace id")
    public void getCorrelationIdWithIdTest() {
        // arrange
        String expected = "traceId";
        when(tracer.currentSpan().context().traceId()).thenReturn(expected);


        // act
        var actual = correlationIDHandler.getCorrelationId();

        // assert
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("getCorrelationId without trace returns empty string")
    public void getCorrelationIdWithoutIdTest() {
        // arrange

        // act
        var actual = correlationIDHandler.getCorrelationId();

        // assert
        Assertions.assertThat(actual).isEmpty();
    }
}