package by.testtask.balancehub.mappers;

import by.testtask.balancehub.domain.Transfer;
import by.testtask.balancehub.dto.redis.TransferDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TransferMapper {

    @Mappings({
            @Mapping(target = "fromUserId", source = "fromAccount.user.id"),
            @Mapping(target = "toUserId", source = "toAccount.user.id"),
            @Mapping(target = "fromAccountId", source = "fromAccount.id"),
            @Mapping(target = "toAccountId", source = "toAccount.id"),
    })
    TransferDTO toDTO(Transfer transfer);

    @Mappings({
            @Mapping(target = "fromAccount", ignore = true),
            @Mapping(target = "toAccount", ignore = true)
    })
    Transfer toEntity(TransferDTO transferDTO);

}
