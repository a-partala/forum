package net.partala.forum.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {

        log.error("Handle entityNotFoundException", e);

        var errorDto = new ErrorResponse(
                "Entity not found",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {

        log.error("Handle handleAccessDeniedException", e);

        var errorDto = new ErrorResponse(
                "Access denied",
                "You don't have permission to access this operation",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {

        log.error("Handle exception", e);

        var errorDto = new ErrorResponse(
                "Internal server error",
                "Something went wrong",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);
    }

    @ExceptionHandler(exception = {
            BadCredentialsException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(Exception e) {

        log.error("Handle unauthorized", e);

        var errorDto = new ErrorResponse(
                "Unauthorized",
                "Incorrect login or password",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorDto);
    }

    @ExceptionHandler(exception = {
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            HandlerMethodValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception e) {

        log.error("Handle badRequest", e);

        var errorDto = new ErrorResponse(
                "Bad request",
                "Incorrect request format or data",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }

    @ExceptionHandler(exception = {
            IllegalStateException.class,
            IllegalArgumentException.class,
            NoSuchElementException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessException(RuntimeException e) {

        log.error("Handle business logic exception", e);

        var errorDto = new ErrorResponse(
                "Bad request",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }
}
