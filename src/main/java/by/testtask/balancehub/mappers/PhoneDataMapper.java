package by.testtask.balancehub.mappers;

import by.testtask.balancehub.domain.PhoneData;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.common.PhoneDataDTO;
import org.mapstruct.*;

import java.util.Collection;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PhoneDataMapper {

    @Mappings({
            @Mapping(target = "user", ignore = true)
    })
    PhoneData toEntity(final PhoneDataDTO phoneDataDTO);

    @Mappings({
            @Mapping(target = "id", source = "phoneDataDTO.id"),
            @Mapping(target = "user", source = "owner")
    })
    PhoneData toEntity(final PhoneDataDTO phoneDataDTO, final User owner);

    @Mappings({
            @Mapping(target = "userId", source = "user.id")
    })
    PhoneDataDTO toDTO(final PhoneData phoneData);

    Collection<PhoneData> allToEntities(final Collection<PhoneDataDTO> phoneDataDTOList);

    Collection<PhoneDataDTO> allToDTOs(final Collection<PhoneData> phoneDataList);

}
