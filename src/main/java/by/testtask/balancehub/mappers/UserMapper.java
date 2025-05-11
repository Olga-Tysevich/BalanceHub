package by.testtask.balancehub.mappers;

import by.testtask.balancehub.domain.EmailData;
import by.testtask.balancehub.domain.PhoneData;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.dto.elasticsearch.UserIndex;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mappings({
            @Mapping(target = "dateOfBirthday", dateFormat = "dd.MM.yyyy")
    })
    UserDTO toDto(User user);

    @Mappings({
            @Mapping(target = "password", ignore = true)
    })
    User toEntity(UserDTO dto);

    @Mappings({
            @Mapping(target = "phoneNumber", source = "phone"),
            @Mapping(target = "user", ignore = true)
    })
    PhoneData toPhoneEntity(UserDTO.InnerPhoneData dto);

    @Mappings({
            @Mapping(target = "phone", source = "phoneNumber")
    })
    UserDTO.InnerPhoneData toPhoneDto(PhoneData entity);

    @Mappings({
            @Mapping(target = "user", ignore = true)
    })
    EmailData toEmailEntity(UserDTO.InnerEmailData dto);

    UserDTO.InnerEmailData toEmailDto(EmailData entity);

    @Mappings({
            @Mapping(source = "phones", target = "phones", qualifiedByName = "mapPhones"),
            @Mapping(source = "emails", target = "emails", qualifiedByName = "mapEmails")
    })
    UserIndex toUserIndex(User entity);

    @Mappings({
            @Mapping(source = "phones", target = "phones", qualifiedByName = "mapPhonesFromIndex"),
            @Mapping(source = "emails", target = "emails", qualifiedByName = "mapEmailsFromIndex")
    })
    UserDTO toUserDTO(UserIndex index);

    @Named("mapPhones")
    default List<UserIndex.PhoneIndex> mapPhones(Set<PhoneData> phones) {
        if (Objects.isNull(phones)) return null;
        return phones.stream()
                .map(p -> UserIndex.PhoneIndex.builder()
                        .id(p.getId())
                        .phone(p.getPhoneNumber())
                        .build())
                .collect(Collectors.toList());
    }

    @Named("mapEmails")
    default List<UserIndex.EmailIndex> mapEmails(Set<EmailData> emails) {
        if (Objects.isNull(emails)) return null;
        return emails.stream()
                .map(e -> UserIndex.EmailIndex.builder()
                        .id(e.getId())
                        .email(e.getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    @Named("mapPhonesFromIndex")
    default Set<UserDTO.InnerPhoneData> mapPhoneIndexList(List<UserIndex.PhoneIndex> phones) {
        if (Objects.isNull(phones)) return null;
        return phones.stream()
                .map(p -> new UserDTO.InnerPhoneData(p.getId(), p.getPhone()))
                .collect(Collectors.toSet());
    }

    @Named("mapEmailsFromIndex")
    default Set<UserDTO.InnerEmailData> mapEmailIndexList(List<UserIndex.EmailIndex> emails ) {
        if (Objects.isNull(emails)) return null;
        return emails.stream()
                .map(e -> new UserDTO.InnerEmailData(e.getId(), e.getEmail()))
                .collect(Collectors.toSet());
    }

    @AfterMapping
    default void linkUserToPhonesAndEmails(@MappingTarget User user) {
        if (user.getPhones() != null) {
            user.getPhones().forEach(phone -> phone.setUser(user));
        }
        if (user.getEmails() != null) {
            user.getEmails().forEach(email -> email.setUser(user));
        }
    }
}
