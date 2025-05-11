package by.testtask.balancehub.dto.req;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

import static by.testtask.balancehub.utils.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchReq {
    @Size(max = 500, message = NAME_CANNOT_BE_GZ_500)
    private String name;
    @Pattern(regexp = REGEXP_EMAIL, message = INVALID_EMAIL_MESSAGE)
    private String email;
    @Pattern(regexp = REGEXP_PHONE, message = INVALID_PHONE_FORMAT)
    private String phone;
    @Past(message = DATE_OF_BIRTHDAY_MUST_BE_IN_PAST)
    private LocalDate dateOfBirth;
    @Min(value = 0, message = "Page must be a positive integer")
    private int page;
    @Min(value = 1, message = "Size must be a positive integer")
    @Builder.Default
    private int size = 10;

    public boolean searchByAllParams() {
        return Objects.nonNull(name) && Objects.nonNull(email)
                && Objects.nonNull(phone) && Objects.nonNull(dateOfBirth);
    }

    public boolean searchByName() {
        return Objects.nonNull(name) && !searchByAllParams();
    }

    public boolean searchByPhone() {
        return Objects.nonNull(phone) && !searchByAllParams();
    }

    public boolean searchByEmail() {
        return Objects.nonNull(email) && !searchByAllParams();
    }

    public boolean searchByDateOfBirth() {
        return Objects.nonNull(dateOfBirth) && !searchByAllParams();
    }
}
