package by.testtask.balancehub.exceptions.handlers;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDetail {

    private final String fieldName;
    private final String message;

}