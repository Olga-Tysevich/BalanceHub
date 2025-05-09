package by.testtask.balancehub.domain;

import by.testtask.balancehub.utils.validators.PositiveBalance;
import jakarta.persistence.*;
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
@Table(name = "account")
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
    @PositiveBalance
    private BigDecimal balance;

}
