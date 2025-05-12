package by.testtask.balancehub.conf.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public abstract class DefaultHandler {

    public void handle(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        log.info("Start handling request: {}", request.getRequestURI());

        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus());
        problemDetail.setTitle(errorTitle());
        problemDetail.setDetail(errorMessage());

        log.debug("ProblemDetail created with status: {}, title: {}, detail: {}",
                problemDetail.getStatus(), problemDetail.getTitle(), problemDetail.getDetail());

        response.setStatus(httpServletResponse());
        response.setContentType(mediaType());

        try (OutputStream os = response.getOutputStream()) {
            new ObjectMapper().writeValue(os, problemDetail);
            os.flush();
        } catch (Exception e) {
            log.error("Error while writing response for request: {}", request.getRequestURI(), e);
        }
    }

    protected abstract HttpStatus httpStatus();
    protected abstract String errorTitle();
    protected abstract String errorMessage();
    protected abstract int httpServletResponse();
    protected abstract String mediaType();
}