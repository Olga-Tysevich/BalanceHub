package by.testtask.balancehub.domain;

import by.testtask.balancehub.utils.validators.CollectionSize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

import static by.testtask.balancehub.utils.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userIdSeq")
    @SequenceGenerator(name = "userIdSeq", sequenceName = "user_id_seq", allocationSize = 1)
    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @Column(nullable = false, length = 500)
    @NotBlank(message = NAME_CANNOT_BE_EMPTY)
    @Size(max = 500, message = NAME_CANNOT_BE_GZ_500)
    private String name;

    @Column
    @Past(message = DATE_OF_BIRTHDAY_MUST_BE_IN_PAST)
    private LocalDate dateOfBirthday;

    @Column(length = 500)
    @Size(min = 8, max = 500, message = INVALID_PASSWORD_LENGTH)
    private String password;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE, CascadeType.DETACH, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @CollectionSize(message = EMPTY_PHONE_SET)
    private Set<PhoneData> phones;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE, CascadeType.DETACH, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @CollectionSize(message = EMPTY_EMAIL_SET)
    private Set<EmailData> emails;

}
