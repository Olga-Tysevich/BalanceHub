package by.testtask.balancehub.dto.req;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static by.testtask.balancehub.utils.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchReq {
    @NotBlank(message = NAME_CANNOT_BE_EMPTY)
    @Size(max = 500, message = NAME_CANNOT_BE_GZ_500)
    private String name;
    @NotBlank(message = EMAIL_CANNOT_BE_NULL_OR_EMPTY)
    @Pattern(regexp = REGEXP_EMAIL, message = INVALID_EMAIL_MESSAGE)
    private String email;
    @NotBlank(message = PHONE_CANNOT_BE_NULL_OR_EMPTY)
    @Pattern(regexp = REGEXP_PHONE, message = INVALID_PHONE_FORMAT)
    private String phone;
    @Past(message = DATE_OF_BIRTHDAY_MUST_BE_IN_PAST)
    private LocalDate dateOfBirth;
    @Min(value = 1, message = "Page must be a positive integer")
    private Integer page;
    @Min(value = 1, message = "Size must be a positive integer")
    private Integer size;
}
