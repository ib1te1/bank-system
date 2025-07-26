package ru.troshin.statement.mapper;

import org.mapstruct.Mapper;
import ru.troshin.statement.deal.dto.LoanStatementRequestDto;

@Mapper(componentModel = "spring")
public interface LoanReqMapper {

    LoanStatementRequestDto toDealLoanReq(ru.troshin.statement.dto.LoanStatementRequestDto dto);

    ru.troshin.statement.dto.LoanStatementRequestDto toStatementLoanReq(LoanStatementRequestDto dto);
}
