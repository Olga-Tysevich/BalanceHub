package by.testtask.balancehub.domain;

import by.testtask.balancehub.utils.validators.CollectionSize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static by.testtask.balancehub.utils.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User implements UserDetails {
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

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE, CascadeType.DETACH, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @CollectionSize(message = EMPTY_PHONE_SET)
    private Set<PhoneData> phones;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE, CascadeType.DETACH, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @CollectionSize(message = EMPTY_EMAIL_SET)
    private Set<EmailData> emails;

    @Transient
    private final Set<GrantedAuthority> roleSet = Set.of(new SimpleGrantedAuthority("USER"));

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleSet;
    }

    @Override
    public String getUsername() {
        return name;
    }
}
