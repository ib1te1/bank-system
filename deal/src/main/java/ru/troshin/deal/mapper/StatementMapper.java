package ru.troshin.deal.mapper;

import org.mapstruct.Mapper;
import ru.troshin.deal.calculator.dto.LoanStatementRequestDto;
import ru.troshin.deal.dto.StatementDto;
import ru.troshin.deal.entity.Statement;

@Mapper(componentModel = "spring")
public interface StatementMapper {
    LoanStatementRequestDto toCalcLoanStatement(ru.troshin.deal.dto.LoanStatementRequestDto dealLoanStatement);

    StatementDto toStatementDto(Statement statement);
    Statement toStatement(StatementDto statementDto);
}
