package by.testtask.balancehub.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static by.testtask.balancehub.utils.Constants.*;
import static by.testtask.balancehub.utils.Constants.INVALID_PHONE_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneDataDTO {
    private Long id;

    @NotNull(message = USER_ID_CANNOT_BE_NULL)
    private Long userId;

    @NotBlank(message = PHONE_CANNOT_BE_NULL_OR_EMPTY)
    @Pattern(regexp = REGEXP_PHONE, message = INVALID_PHONE_FORMAT)
    private String phoneNumber;
}
