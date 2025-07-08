package ru.troshin.deal.mapper;

import org.mapstruct.Mapper;
import ru.troshin.deal.dto.LoanOfferDto;

@Mapper(componentModel = "spring")
public interface LoanOfferMapper {
    LoanOfferDto toDealLoanOffer(ru.troshin.deal.calculator.dto.LoanOfferDto calcLoanOffer);
}
