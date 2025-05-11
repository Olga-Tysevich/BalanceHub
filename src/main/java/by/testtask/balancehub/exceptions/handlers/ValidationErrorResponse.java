package by.testtask.balancehub.exceptions.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValidationErrorResponse {

    private final List<ErrorDetail> violations;

}