package by.testtask.balancehub.exceptions.handlers;

import by.testtask.balancehub.exceptions.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler({
            AccessDeniedException.class,
            ProhibitedException.class
    })
    public ResponseEntity<?> accessExceptions(AccessDeniedException e) {
        return buildExceptionResponse(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler({
            HttpMediaTypeException.class,
            HttpMessageNotReadableException.class,
            MissingRequestCookieException.class,
            MissingRequestHeaderException.class,
            MissingRequestValueException.class
    })
    public ResponseEntity<?> badRequestExceptions(Exception e) {
        return buildExceptionResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            UsernameNotFoundException.class,
            InternalAuthenticationServiceException.class,
            NoResourceFoundException.class
    })
    public ResponseEntity<?> notFoundExceptions(Exception e) {
        return buildExceptionResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({
            EmailAlreadyInUse.class,
            PhoneAlreadyInUse.class,
            AccountAlreadyExists.class
    })
    public ResponseEntity<?> conflictExceptions(Exception e) {
        return buildExceptionResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler({
            UnauthorizedException.class,
            InvalidRefreshTokenException.class,
            AuthenticationCredentialsNotFoundException.class
    })
    public ResponseEntity<?> unauthorizedExceptions(Exception e) {
        return buildExceptionResponse(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationExceptions(MethodArgumentNotValidException e) {
        log.error("Validation Failed: {}", e.getMessage(), e);
        BindingResult result = e.getBindingResult();
        List<ErrorDetail> errorDetails = result.getFieldErrors().stream()
                .map(fieldError -> new ErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrorResponse(errorDetails));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> internalServerError(Exception e) {
        log.error("Internal Server Error: {}", e.getMessage(), e);
        return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    private ResponseEntity<Object> buildExceptionResponse(HttpStatus status, Exception e) {
        log.error("Exception occurred: {} - Status: {} - Message: {}",
                e.getClass().getSimpleName(), status, e.getMessage(), e);

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                status.value(),
                e.getMessage(),
                List.of(e.getClass().getSimpleName()),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(exceptionResponse);
    }
}
