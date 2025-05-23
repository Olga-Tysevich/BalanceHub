package by.testtask.balancehub.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import static by.testtask.balancehub.utils.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "email_data")
public class EmailData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emailDataIdSeq")
    @SequenceGenerator(name = "emailDataIdSeq", sequenceName = "email_data_id_seq", allocationSize = 1)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = USER_CANNOT_BE_NULL)
    private User user;

    @Column(nullable = false, length = 200, unique = true)
    @NotBlank(message = EMAIL_CANNOT_BE_NULL_OR_EMPTY)
    @Pattern(regexp = REGEXP_EMAIL, message = INVALID_EMAIL_MESSAGE)
    private String email;
}