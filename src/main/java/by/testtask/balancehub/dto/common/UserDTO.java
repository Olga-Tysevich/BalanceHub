package by.testtask.balancehub.dto.common;

import by.testtask.balancehub.conf.jackson.CustomLocalDateDeserializer;
import by.testtask.balancehub.domain.EmailData;
import by.testtask.balancehub.domain.PhoneData;
import by.testtask.balancehub.utils.validators.CollectionSize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static by.testtask.balancehub.utils.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @NotBlank(message = NAME_CANNOT_BE_EMPTY)
    @Size(max = 500, message = NAME_CANNOT_BE_GZ_500)
    private String name;

    @Past(message = DATE_OF_BIRTHDAY_MUST_BE_IN_PAST)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate dateOfBirthday;

    @CollectionSize(message = EMPTY_PHONE_SET)
    private Set<InnerPhoneData> phones;

    @CollectionSize(message = EMPTY_PHONE_SET)
    private Set<InnerEmailData> emails;

    public void setPhoneFromDTO(Set<PhoneDataDTO> phones) {
        this.phones = phones.stream()
                .map(InnerPhoneData::new)
                .collect(Collectors.toSet());
    }

    public void setEmailFromDTO(Set<EmailDataDTO> emails) {
        this.emails = emails.stream()
                .map(InnerEmailData::new)
                .collect(Collectors.toSet());
    }

    public void setPhoneFrom(Set<PhoneData> phones) {
        this.phones = phones.stream()
                .map(InnerPhoneData::new)
                .collect(Collectors.toSet());
    }

    public void innerEmailFrom(Set<EmailData> emails) {
        this.emails = emails.stream()
                .map(InnerEmailData::new)
                .collect(Collectors.toSet());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InnerPhoneData {
        private Long id;
        private String phone;

        public InnerPhoneData(PhoneDataDTO phone) {
            this.id = phone.getId();
            this.phone = phone.getPhoneNumber();
        }

        public InnerPhoneData(PhoneData phone) {
            this.id = phone.getId();
            this.phone = phone.getPhoneNumber();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InnerEmailData {
        private Long id;
        private String email;

        public InnerEmailData(EmailDataDTO email) {
            this.id = email.getId();
            this.email = email.getEmail();
        }

        public InnerEmailData(EmailData email) {
            this.id = email.getId();
            this.email = email.getEmail();
        }
    }

}
