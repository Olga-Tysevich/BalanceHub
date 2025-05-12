package by.testtask.balancehub.dto.common;

import by.testtask.balancehub.conf.jackson.CustomLocalDateDeserializer;
import by.testtask.balancehub.conf.jackson.CustomLocalDateSerializer;
import by.testtask.balancehub.utils.validators.CollectionSize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

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

    @Past(message = DATE_OF_BIRTH_MUST_BE_IN_PAST)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate dateOfBirth;

    @CollectionSize(message = EMPTY_PHONE_SET)
    private Set<InnerPhoneData> phones;

    @CollectionSize(message = EMPTY_PHONE_SET)
    private Set<InnerEmailData> emails;

    private InnerAccountData account;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InnerPhoneData {
        private Long id;
        private String phone;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InnerEmailData {
        private Long id;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InnerAccountData {
        private Long id;
        private BigDecimal balance;
    }

}
