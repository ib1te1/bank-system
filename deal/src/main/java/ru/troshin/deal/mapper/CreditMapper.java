package ru.troshin.deal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.troshin.deal.dto.CreditDto;
import ru.troshin.deal.entity.Credit;

@Mapper(componentModel = "spring")
public interface CreditMapper {

    @Mapping(source = "isInsuranceEnabled", target = "insuranceEnabled")
    @Mapping(source = "isSalaryClient", target = "salaryClient")
    Credit toCredit(CreditDto creditDto);

    @Mapping(source = "insuranceEnabled", target = "isInsuranceEnabled")
    @Mapping(source = "salaryClient", target = "isSalaryClient")
    CreditDto toDto(Credit entity);


    CreditDto toDealCredit(ru.troshin.deal.calculator.dto.CreditDto calcDto);
}
