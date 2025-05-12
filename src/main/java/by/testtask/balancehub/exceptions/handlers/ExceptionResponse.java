package by.testtask.balancehub.exceptions.handlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ExceptionResponse {

    private final Integer HttpStatusCode;
    private final String ExceptionMessage;
    private final List<String> ExceptionDetails;
    private final LocalDateTime TimeStamp;

}