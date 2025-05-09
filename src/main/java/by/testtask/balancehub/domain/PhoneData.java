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
@Table(name = "phone_data")
public class PhoneData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phoneDataIdSeq")
    @SequenceGenerator(name = "phoneDataIdSeq", sequenceName = "phone_data_id_seq", allocationSize = 1)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = USER_CANNOT_BE_NULL)
    private User user;

    @Column(nullable = false, length = 13, unique = true)
    @NotBlank(message = PHONE_CANNOT_BE_NULL_OR_EMPTY)
    @Pattern(regexp = REGEXP_PHONE, message = INVALID_PHONE_FORMAT)
    private String phoneNumber;
}