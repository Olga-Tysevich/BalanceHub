package by.testtask.balancehub.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import static by.testtask.balancehub.utils.Constants.EMAIL_CANNOT_BE_NULL_OR_EMPTY;
import static by.testtask.balancehub.utils.Constants.PASSWORD_CANNOT_BE_NULL_OR_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDTO {

    @NotBlank(message = EMAIL_CANNOT_BE_NULL_OR_EMPTY)
    private String emailOrPhone;

    @NotBlank(message = PASSWORD_CANNOT_BE_NULL_OR_EMPTY)
    private String password;

}
