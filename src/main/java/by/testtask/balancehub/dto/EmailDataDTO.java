package by.testtask.balancehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static by.testtask.balancehub.utils.Constants.*;
import static by.testtask.balancehub.utils.Constants.INVALID_EMAIL_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDataDTO {
    private Long id;

    @NotNull(message = USER_ID_CANNOT_BE_NULL)
    private Long userId;

    @NotBlank(message = EMAIL_CANNOT_BE_NULL_OR_EMPTY)
    @Pattern(regexp = REGEXP_EMAIL, message = INVALID_EMAIL_MESSAGE)
    private String email;
}
