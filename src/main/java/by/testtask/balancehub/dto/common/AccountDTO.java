package by.testtask.balancehub.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static by.testtask.balancehub.utils.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {
    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @NotNull(message = USER_ID_CANNOT_BE_NULL)
    private Long userId;

    @NotNull(message = BALANCE_CANNOT_BE_NULL)
    private BigDecimal balance;

    @NotNull(message = INITIAL_BALANCE_MUST_BE_POSITIVE)
    private BigDecimal initialBalance;

}
