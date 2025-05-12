package by.testtask.balancehub.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static by.testtask.balancehub.utils.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransferReq {

    @NotNull(message = USER_ID_CANNOT_BE_NULL)
    private Long fromAccountId;

    @NotNull(message = USER_ID_CANNOT_BE_NULL)
    private Long toAccountId;

    @NotNull(message = BALANCE_CANNOT_BE_NULL)
    private BigDecimal amount;

}
