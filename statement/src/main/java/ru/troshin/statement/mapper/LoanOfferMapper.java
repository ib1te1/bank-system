package ru.troshin.statement.mapper;

import org.mapstruct.Mapper;
import ru.troshin.statement.deal.dto.LoanOfferDto;

@Mapper(componentModel = "spring")
public interface LoanOfferMapper {

    LoanOfferDto toDealDto(ru.troshin.statement.dto.LoanOfferDto dto);

    ru.troshin.statement.dto.LoanOfferDto toStatementOffer(LoanOfferDto dto);
}
