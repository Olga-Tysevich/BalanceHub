package by.testtask.balancehub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @Column(name = "bonus_balance",nullable = false, precision = 19, scale = 2)
    @NotNull(message = BALANCE_CANNOT_BE_NULL)
    @DecimalMin(value = "0.00", message = BALANCE_MUST_BE_POSITIVE)
    @Builder.Default
    private BigDecimal bonusBalance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull(message = BONUS_BALANCE_MUST_BE_POSITIVE)
    @DecimalMin(value = "0.00", message = BONUS_BALANCE_MUST_BE_POSITIVE)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "bonus_hold", nullable = false, precision = 19, scale = 2)
    @NotNull(message = HOLD_CANNOT_BE_NULL)
    @DecimalMin(value = "0.00", message = HOLD_MUST_BE_POSITIVE)
    @Builder.Default
    @Setter(AccessLevel.PROTECTED)
    private BigDecimal bonusHold = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull(message = HOLD_CANNOT_BE_NULL)
    @DecimalMin(value = "0.00", message = HOLD_MUST_BE_POSITIVE)
    @Builder.Default
    @Setter(AccessLevel.PROTECTED)
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

    public BigDecimal getBonusBalance() {
        return bonusBalance.subtract(bonusHold);
    }

    public BigDecimal getBalance() {
        return balance.subtract(hold);
    }

    public void addToHold(BigDecimal amount) {
        if (this.hold.add(amount).compareTo(this.balance) > 0) {
            throw new IllegalArgumentException("Insufficient available balance to hold funds.");
        }
        this.hold = this.hold.add(amount);
    }

    public void addToBonusHold(BigDecimal amount) {
        if (this.bonusHold.add(amount).compareTo(this.bonusBalance) > 0) {
            throw new IllegalArgumentException("Insufficient available bonus balance to bonus hold funds.");
        }
        this.bonusHold = this.bonusBalance.add(amount);
    }

    public void releaseFromHold(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount to release must be positive.");
        }
        if (this.hold.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Cannot release more than is held.");
        }
        this.hold = this.hold.subtract(amount);
        this.balance = this.balance.subtract(amount);
    }

    public void releaseFromBonusHold(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount to release must be positive.");
        }
        if (this.bonusHold.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Cannot release more than is held in bonus.");
        }
        this.bonusHold = this.bonusHold.subtract(amount);
        this.bonusBalance = this.bonusBalance.subtract(amount);
    }

}
