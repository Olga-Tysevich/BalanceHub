package by.testtask.balancehub.exceptions.handlers;

import java.util.List;

public record ValidationErrorResponse(List<ErrorDetail> violations) {

}