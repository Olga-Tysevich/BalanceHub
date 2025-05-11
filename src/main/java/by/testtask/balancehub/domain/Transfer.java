package by.testtask.balancehub.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "from_account_id", nullable = false)
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id", nullable = false)
    private Account toAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransferStatus status = TransferStatus.PENDING;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Version
    private Long version;

}