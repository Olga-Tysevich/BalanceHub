package by.testtask.balancehub.mappers;

import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.common.AccountDTO;
import org.mapstruct.*;

import java.util.Collection;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper {
    @Mappings({
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "hold", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    Account toEntity(final AccountDTO accountDTO);

    @Mappings({
            @Mapping(target = "id", source = "accountDTO.id"),
            @Mapping(target = "hold", ignore = true),
            @Mapping(target = "user", source = "owner"),
            @Mapping(target = "version", ignore = true)
    })
    Account toEntity(final AccountDTO accountDTO, final User owner);

    @Mappings({
            @Mapping(target = "userId", source = "user.id")
    })
    AccountDTO toDTO(final Account account);

    Collection<Account> allToEntities(final Collection<AccountDTO> accountDTOs);

    Collection<AccountDTO> allToDTOs(final Collection<Account> accounts);
}
