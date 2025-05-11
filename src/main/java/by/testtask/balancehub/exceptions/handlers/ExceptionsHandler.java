package by.testtask.balancehub.exceptions.handlers;

import by.testtask.balancehub.exceptions.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
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

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessExceptions(AccessDeniedException e) {
        return buildExceptionResponse(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler({
            HttpMediaTypeException.class,
            PasswordMismatchException.class,
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
            PhoneAlreadyInUse.class
    })
    public ResponseEntity<?> conflictExceptions(Exception e) {
        return buildExceptionResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler({
            UnauthorizedException.class,
            InvalidRefreshTokenException.class
    })
    public ResponseEntity<?> unauthorizedExceptions(Exception e) {
        return buildExceptionResponse(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationExceptions(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        List<ErrorDetail> errorDetails = result.getFieldErrors().stream()
                .map(fieldError -> new ErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrorResponse(errorDetails));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> internalServerError(Exception e) {
        return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    private ResponseEntity<Object> buildExceptionResponse(HttpStatus status, Exception e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                status.value(),
                e.getMessage(),
                List.of(e.getClass().getSimpleName()),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(exceptionResponse);
    }
}
