package by.testtask.balancehub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static by.testtask.balancehub.utils.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transferIdSeq")
    @SequenceGenerator(name = "transferIdSeq", sequenceName = "transfer_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_account_id", nullable = false)
    private Account fromAccount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_account_id", nullable = false)
    private Account toAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    @DecimalMin(value = "0.00", message = TRANSFER_AMOUNT_BE_POSITIVE)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransferStatus status = TransferStatus.PENDING;

    @Column(name = "created_at",nullable = false)
    @PastOrPresent(message = TRANSFER_DATE_MUST_BE_IN_PAST)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    @PastOrPresent(message = TRANSFER_DATE_MUST_BE_IN_PAST)
    private LocalDateTime confirmedAt;

    @Version
    private Long version;

}