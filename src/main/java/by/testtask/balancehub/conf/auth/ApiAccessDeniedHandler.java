package by.testtask.balancehub.conf.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static by.testtask.balancehub.utils.Constants.ACCESS_DENIED;
import static by.testtask.balancehub.utils.Constants.ACCESS_DENIED_MESSAGE;

public class ApiAccessDeniedHandler extends DefaultHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        //TODO добавить лог
        super.handle(request, response);
    }

    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    protected String errorTitle() {
        return ACCESS_DENIED;
    }

    @Override
    protected String errorMessage() {
        return ACCESS_DENIED_MESSAGE;
    }

    @Override
    protected int httpServletResponse() {
        return HttpServletResponse.SC_FORBIDDEN;
    }

    @Override
    protected String mediaType() {
        return MediaType.APPLICATION_PROBLEM_JSON_VALUE;
    }
}