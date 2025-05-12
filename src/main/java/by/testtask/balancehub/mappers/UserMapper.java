package by.testtask.balancehub.mappers;

import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.EmailData;
import by.testtask.balancehub.domain.PhoneData;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.dto.elasticsearch.UserIndexDTO;
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
            @Mapping(source = "phones", target = "phones", qualifiedByName = "mapPhonesFromEntity"),
            @Mapping(source = "emails", target = "emails", qualifiedByName = "mapEmailsFromEntity"),
            @Mapping(source = "account", target = "account", qualifiedByName = "mapAccountFromEntity")
    })
    UserDTO toUserDTO(User entity);

    UserDTO userToUserDTO(User user);

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
            @Mapping(source = "emails", target = "emails", qualifiedByName = "mapEmails"),
            @Mapping(source = "account", target = "account", qualifiedByName = "mapAccountToIndex")

    })
    UserIndexDTO toUserIndex(User entity);

    @Mappings({
            @Mapping(source = "phones", target = "phones", qualifiedByName = "mapPhonesFromIndex"),
            @Mapping(source = "emails", target = "emails", qualifiedByName = "mapEmailsFromIndex"),
            @Mapping(source = "account", target = "account", qualifiedByName = "mapAccountFromIndex")

    })
    UserDTO toUserDTO(UserIndexDTO index);

    @Named("mapAccountToIndex")
    default UserIndexDTO.AccountIndex mapAccountToIndex(Account account) {
        if (Objects.isNull(account)) return null;
        return UserIndexDTO.AccountIndex.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .build();
    }

    @Named("mapAccountFromIndex")
    default UserDTO.InnerAccountData mapAccountFromIndex(UserIndexDTO.AccountIndex index) {
        if (Objects.isNull(index)) return null;
        return new UserDTO.InnerAccountData(index.getId(), index.getBalance());
    }

    @Named("mapPhones")
    default List<UserIndexDTO.PhoneIndex> mapPhones(Set<PhoneData> phones) {
        if (Objects.isNull(phones)) return null;
        return phones.stream()
                .map(p -> UserIndexDTO.PhoneIndex.builder()
                        .id(p.getId())
                        .phone(p.getPhoneNumber())
                        .build())
                .collect(Collectors.toList());
    }

    @Named("mapEmails")
    default List<UserIndexDTO.EmailIndex> mapEmails(Set<EmailData> emails) {
        if (Objects.isNull(emails)) return null;
        return emails.stream()
                .map(e -> UserIndexDTO.EmailIndex.builder()
                        .id(e.getId())
                        .email(e.getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    @Named("mapPhonesFromIndex")
    default Set<UserDTO.InnerPhoneData> mapPhoneIndexList(List<UserIndexDTO.PhoneIndex> phones) {
        if (Objects.isNull(phones)) return null;
        return phones.stream()
                .map(p -> new UserDTO.InnerPhoneData(p.getId(), p.getPhone()))
                .collect(Collectors.toSet());
    }

    @Named("mapEmailsFromIndex")
    default Set<UserDTO.InnerEmailData> mapEmailIndexList(List<UserIndexDTO.EmailIndex> emails) {
        if (Objects.isNull(emails)) return null;
        return emails.stream()
                .map(e -> new UserDTO.InnerEmailData(e.getId(), e.getEmail()))
                .collect(Collectors.toSet());
    }

    @Named("mapPhonesFromEntity")
    default Set<UserDTO.InnerPhoneData> mapPhonesFromEntity(Set<PhoneData> phones) {
        if (Objects.isNull(phones)) return null;
        return phones.stream()
                .map(p -> new UserDTO.InnerPhoneData(p.getId(), p.getPhoneNumber()))
                .collect(Collectors.toSet());
    }

    @Named("mapEmailsFromEntity")
    default Set<UserDTO.InnerEmailData> mapEmailsFromEntity(Set<EmailData> emails) {
        if (Objects.isNull(emails)) return null;
        return emails.stream()
                .map(e -> new UserDTO.InnerEmailData(e.getId(), e.getEmail()))
                .collect(Collectors.toSet());
    }

    @Named("mapAccountFromEntity")
    default UserDTO.InnerAccountData mapAccountFromEntity(Account account) {
        if (Objects.isNull(account)) return null;
        return new UserDTO.InnerAccountData(account.getId(), account.getBalance());
    }

}
