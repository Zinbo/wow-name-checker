package com.stacktobasics.wownamechecker.infra.exceptionhandler;

import feign.FeignException;
import feign.Request;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Map;
import java.util.Set;

import static com.stacktobasics.wownamechecker.TestHelper.similarTime;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpExceptionHandlerTest {

    public static final String CORRELATION_ID = "12345";
    private CorrelationIDHandler correlationIDHandler = Mockito.mock(CorrelationIDHandler.class);

    private HttpExceptionHandler httpExceptionHandler = new HttpExceptionHandler(correlationIDHandler);

    @BeforeEach
    void setUp() {
        when(correlationIDHandler.getCorrelationId()).thenReturn(CORRELATION_ID);
    }

    @Test
    @DisplayName("handleIntegrityViolationException with exception returns bad request with exception")
    public void handleIntegrityViolationExceptionTest() {
        // arrange
        var exception = new DataIntegrityViolationException("field not valid");
        var expected = new ExceptionResponse(HttpStatus.BAD_REQUEST, exception, CORRELATION_ID);
        
        // act
        var actual = httpExceptionHandler.handleIntegrityViolationException(exception).getBody();

        // assert

        Assertions.assertThat(actual).usingRecursiveComparison()
                .withEqualsForFields(similarTime(), "datetime")
                .isEqualTo(expected);
    }
    
    @Test
    @DisplayName("requestNotValidException with exception returns bad request with readable exception message")
    public void requestNotValidExceptionTest() {
        // arrange
        BindingResult bindingResult = new DirectFieldBindingResult("hello", "fieldName");
        bindingResult.addError(new FieldError("hello", "fieldName", "not valid"));
        var methodParameter = mock(MethodParameter.class);
        when(methodParameter.toString()).thenReturn("method 'someMethod()'");
        var exception = new MethodArgumentNotValidException(methodParameter, bindingResult);
        var expected = new ExceptionResponse(HttpStatus.BAD_REQUEST, new IllegalArgumentException("""
                method 'someMethod()' failed validation with 1 errors.
                Errors: [
                Field: "fieldName", Message: "not valid"
                ]"""), CORRELATION_ID);

        // act
        var actual = httpExceptionHandler.requestNotValidException(exception).getBody();

        // assert
        Assertions.assertThat(actual).usingRecursiveComparison()
                .withEqualsForFields(similarTime(), "datetime")
                .isEqualTo(expected);
    }
    
    @Test
    @DisplayName("missingParamException with exception returns bad request with exception")
    public void missingParamExceptionTest() {
        // arrange
        var exception = new MissingServletRequestParameterException("param1", "String");
        var expected = new ExceptionResponse(HttpStatus.BAD_REQUEST, exception, CORRELATION_ID);

        // act
        var actual = httpExceptionHandler.missingParamException(exception).getBody();

        // assert

        Assertions.assertThat(actual).usingRecursiveComparison()
                .withEqualsForFields(similarTime(), "datetime")
                .isEqualTo(expected);
    }
    
    @Test
    @DisplayName("constraintViolationException with exception returns bad request with exception")
    public void constraintViolationExceptionTest() {
        // arrange
        var constraintViolation = mock(ConstraintViolation.class);
        when(constraintViolation.getMessage()).thenReturn("some violation");
        var exception = new ConstraintViolationException(Set.<ConstraintViolation<?>>of(constraintViolation));
        var expected = new ExceptionResponse(HttpStatus.BAD_REQUEST, exception, CORRELATION_ID);

        // act
        var actual = httpExceptionHandler.constraintViolationException(exception).getBody();

        // assert
        Assertions.assertThat(actual).usingRecursiveComparison()
                .withEqualsForFields(similarTime(), "datetime")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("handleFeign with exception with body returns response with feign status and body")
    public void handleFeignWithBodyTest() {
        // arrange
        Request request = Request.create(Request.HttpMethod.GET, "url", Map.of(), Request.Body.create("body"), null);
        String expectedMessage = "some error";
        var exception = new FeignException.BadRequest("internal server error", request, expectedMessage.getBytes(), null);
        var expected = new ExceptionResponse(HttpStatus.BAD_REQUEST, new IllegalArgumentException(expectedMessage), CORRELATION_ID);

        // act
        var actual = httpExceptionHandler.handleFeign(exception).getBody();

        // assert

        Assertions.assertThat(actual).usingRecursiveComparison()
                .withEqualsForFields(similarTime(), "datetime")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("handleFeign with exception without body returns response with feign status and empty message")
    public void handleFeignWithoutBodyTest() {
        // arrange
        Request request = Request.create(Request.HttpMethod.GET, "url", Map.of(), Request.Body.create("body"), null);
        var exception = new FeignException.BadRequest("internal server error", request, null, null);
        var expected = new ExceptionResponse(HttpStatus.BAD_REQUEST, new IllegalArgumentException(""), CORRELATION_ID);

        // act
        var actual = httpExceptionHandler.handleFeign(exception).getBody();

        // assert

        Assertions.assertThat(actual).usingRecursiveComparison()
                .withEqualsForFields(similarTime(), "datetime")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("missingBodyException with exception returns bad request with exception")
    public void missingBodyExceptionTest() {
        // arrange
        var exception = new HttpMessageNotReadableException("message not readable");
        var expected = new ExceptionResponse(HttpStatus.BAD_REQUEST, exception, CORRELATION_ID);

        // act
        var actual = httpExceptionHandler.missingBodyException(exception).getBody();

        // assert

        Assertions.assertThat(actual).usingRecursiveComparison()
                .withEqualsForFields(similarTime(), "datetime")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("handleAllOtherExceptions with exception returns 500 with exception")
    public void handleAllOtherExceptionsTest() {
        // arrange
        var exception = new RuntimeException("some other error");
        var expected = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, CORRELATION_ID);

        // act
        var actual = httpExceptionHandler.handleAllOtherExceptions(exception).getBody();

        // assert
        Assertions.assertThat(actual).usingRecursiveComparison()
                .withEqualsForFields(similarTime(), "datetime")
                .isEqualTo(expected);
    }

}