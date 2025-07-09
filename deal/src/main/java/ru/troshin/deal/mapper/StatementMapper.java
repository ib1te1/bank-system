package ru.troshin.deal.mapper;

import org.mapstruct.Mapper;
import ru.troshin.deal.calculator.dto.LoanStatementRequestDto;

@Mapper(componentModel = "spring")
public interface StatementMapper {
    LoanStatementRequestDto toCalcLoanStatement(ru.troshin.deal.dto.LoanStatementRequestDto dealLoanStatement);
}
