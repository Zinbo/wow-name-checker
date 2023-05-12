package com.stacktobasics.wownamechecker.infra.exceptionhandler;

import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class HttpExceptionHandler {

    private final CorrelationIDHandler correlationIDHandler;

    public HttpExceptionHandler(CorrelationIDHandler correlationIDHandler) {
        this.correlationIDHandler = correlationIDHandler;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error(exception.getMessage(), exception);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> requestNotValidException(MethodArgumentNotValidException exception) {
        BindingResult results = exception.getBindingResult();
        var sb = new StringBuilder();
        for(var e: results.getFieldErrors()) {
            sb.append(String.format("Field: \"%s\", Message: \"%s\"\n", e.getField(), e.getDefaultMessage()));
        }
        String message = String.format("%s failed validation with %s errors.\n Errors [\n%s]", exception.getParameter(),
                exception.getErrorCount(), sb);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, new IllegalArgumentException(message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> missingParamException(MissingServletRequestParameterException exception) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> constraintViolationException(ConstraintViolationException exception) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ExceptionResponse> handleFeign(FeignException feignException) {
        return buildResponseEntity(HttpStatus.valueOf(feignException.status()),
                new IllegalArgumentException(feignException.responseBody().map(b -> new String(b.array())).orElse("")));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> missingBodyException(HttpMessageNotReadableException exception) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllOtherExceptions(Exception exception) {
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    private ResponseEntity<ExceptionResponse> buildResponseEntity(HttpStatus httpStatus, Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ExceptionResponse(httpStatus, exception, correlationIDHandler.getCorrelationId()), httpStatus);
    }
}
