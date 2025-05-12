package by.testtask.balancehub.mappers;
import by.testtask.balancehub.domain.EmailData;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.common.EmailDataDTO;
import org.mapstruct.*;

import java.util.Collection;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EmailDataMapper {
    @Mappings({
            @Mapping(target = "user", ignore = true)
    })
    EmailData toEntity(final EmailDataDTO emailDataDTO);

    @Mappings({
            @Mapping(target = "id", source = "emailDataDTO.id"),
            @Mapping(target = "user", source = "owner")
    })
    EmailData toEntity(final EmailDataDTO emailDataDTO, final User owner);

    @Mappings({
            @Mapping(target = "userId", source = "user.id")
    })
    EmailDataDTO toDTO(final EmailData emailData);

    Collection<EmailData> allToEntities(Collection<EmailData> emailDataCollection);

    Collection<EmailData> allToDTOs(Collection<EmailData> emailDataCollection);
}
