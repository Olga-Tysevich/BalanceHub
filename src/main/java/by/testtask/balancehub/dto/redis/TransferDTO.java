package by.testtask.balancehub.dto.redis;

import by.testtask.balancehub.domain.TransferStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static by.testtask.balancehub.utils.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferDTO {
    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;
    @DecimalMin(value = "0.00", message = TRANSFER_AMOUNT_BE_POSITIVE)
    private BigDecimal amount;
    @NotNull(message = USER_CANNOT_BE_NULL)
    private Long fromUserId;
    @NotNull(message = USER_CANNOT_BE_NULL)
    private Long toUserId;
    @NotNull(message = ACCOUNT_CANNOT_BE_NULL)
    private Long fromAccountId;
    @NotNull(message = ACCOUNT_CANNOT_BE_NULL)
    private Long toAccountId;
    @PastOrPresent(message = TRANSFER_DATE_MUST_BE_IN_PAST)
    private LocalDateTime createdAt;
    @PastOrPresent(message = TRANSFER_DATE_MUST_BE_IN_PAST)
    private LocalDateTime confirmedAt;
    @NotNull(message = TRANSFER_STATUS_CANNOT_BE_NUL)
    private TransferStatus status;
    @NotNull(message = VERSION_CANNOT_BE_NULL)
    @Builder.Default
    private Long version = 0L;
}