package by.testtask.balancehub.mappers;

import by.testtask.balancehub.domain.EmailData;
import by.testtask.balancehub.domain.PhoneData;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.UserDTO;
import org.mapstruct.*;

import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class UserMapper {

    @Mappings({
            @Mapping(target = "dateOfBirthday", dateFormat = "dd.MM.yyyy")
    })
    public abstract UserDTO toDto(User user);

    @Mappings({
            @Mapping(target = "password", ignore = true)
    })
    public abstract User toEntity(UserDTO dto);

    @Mappings({
            @Mapping(target = "phoneNumber", source = "phone"),
            @Mapping(target = "user", ignore = true)
    })
    public abstract PhoneData toPhoneEntity(UserDTO.InnerPhoneData dto);

    @Mappings({
            @Mapping(target = "phone", source = "phoneNumber")
    })
    public abstract UserDTO.InnerPhoneData toPhoneDto(PhoneData entity);

    @Mappings({
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "user", ignore = true)
    })
    public abstract EmailData toEmailEntity(UserDTO.InnerEmailData dto);

    @Mappings({
            @Mapping(target = "email", source = "email")
    })
    public abstract UserDTO.InnerEmailData toEmailDto(EmailData entity);

    public abstract Set<PhoneData> toPhoneEntitySet(Set<UserDTO.InnerPhoneData> dtos);

    public abstract Set<UserDTO.InnerPhoneData> toPhoneDtoSet(Set<PhoneData> entities);

    public abstract Set<EmailData> toEmailEntitySet(Set<UserDTO.InnerEmailData> dtos);

    public abstract Set<UserDTO.InnerEmailData> toEmailDtoSet(Set<EmailData> entities);

    @AfterMapping
    protected void linkUserToPhonesAndEmails(@MappingTarget User user, UserDTO dto) {
        if (user.getPhones() != null) {
            user.getPhones().forEach(phone -> phone.setUser(user));
        }
        if (user.getEmails() != null) {
            user.getEmails().forEach(email -> email.setUser(user));
        }
    }
}

