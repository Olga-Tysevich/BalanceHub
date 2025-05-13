package by.testtask.balancehub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
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
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountIdSeq")
    @SequenceGenerator(name = "accountIdSeq", sequenceName = "account_id_seq", allocationSize = 1)
    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = USER_CANNOT_BE_NULL)
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull(message = BALANCE_CANNOT_BE_NULL)
    @DecimalMin(value = "0.00", message = BALANCE_MUST_BE_POSITIVE)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull(message = HOLD_MUST_BE_NULL)
    @DecimalMin(value = "0.00", message = HOLD_MUST_BE_POSITIVE)
    @Builder.Default
    private BigDecimal hold = BigDecimal.ZERO;

    @Column(name = "initial_balance", nullable = false, precision = 19, scale = 2)
    @DecimalMin(value = "0.00", message = INITIAL_BALANCE_MUST_BE_POSITIVE)
    @Builder.Default
    private BigDecimal initialBalance = BigDecimal.ZERO;

    @Version
    private Long version;

    public void setBalance(@NotNull
                           @DecimalMin(value = "0.00", message = INITIAL_BALANCE_MUST_BE_POSITIVE)
                           BigDecimal balance) {

        if (this.initialBalance.compareTo(BigDecimal.ZERO) == 0) {
            this.initialBalance = balance;
        }
        this.balance = balance;
    }

}
