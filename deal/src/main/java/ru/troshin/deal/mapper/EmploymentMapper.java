package ru.troshin.deal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.troshin.deal.dto.EmploymentDto;
import ru.troshin.deal.entity.Employment;

@Mapper(componentModel = "spring")
public interface EmploymentMapper {


    @Mapping(source = "employmentStatus", target = "status")
    @Mapping(source = "employmentPosition", target = "position")
    Employment toEmployment(EmploymentDto employmentDto);

    @Mapping(source = "status", target = "employmentStatus")
    @Mapping(source = "position", target = "employmentPosition")
    EmploymentDto toDto(Employment employment);
}
