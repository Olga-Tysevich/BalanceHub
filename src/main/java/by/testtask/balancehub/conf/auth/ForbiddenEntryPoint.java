package by.testtask.balancehub.conf.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static by.testtask.balancehub.utils.Constants.*;

public class ForbiddenEntryPoint extends DefaultHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        super.handle(request, response);
    }

    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    protected String errorTitle() {
        return NOT_AUTHORIZED;
    }

    @Override
    protected String errorMessage() {
        return UNKNOWN_USER;
    }

    @Override
    protected int httpServletResponse() {
        return HttpServletResponse.SC_UNAUTHORIZED;
    }

    @Override
    protected String mediaType() {
        return MediaType.APPLICATION_PROBLEM_JSON_VALUE;
    }
}