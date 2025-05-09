package by.testtask.balancehub.conf.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.io.IOException;
import java.io.OutputStream;


public abstract class DefaultHandler {

    public void handle(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus());
        problemDetail.setTitle(errorTitle());
        problemDetail.setDetail(errorMessage());

        response.setStatus(httpServletResponse());
        response.setContentType(mediaType());

        try (OutputStream os = response.getOutputStream()) {
            new ObjectMapper().writeValue(os, problemDetail);
            os.flush();
        }
    }

    protected abstract HttpStatus httpStatus();
    protected abstract String errorTitle();
    protected abstract String errorMessage();
    protected abstract int httpServletResponse();
    protected abstract String mediaType();
}