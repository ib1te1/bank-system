package ru.troshin.deal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.troshin.deal.dto.LoanStatementRequestDto;
import ru.troshin.deal.entity.Client;
import ru.troshin.deal.entity.Passport;


@Mapper(componentModel = "spring", imports = {Passport.class})
public interface ClientMapper {

    @Mapping(source = "birthdate", target = "birthDate")
    @Mapping(target = "passport", expression = "java(new Passport(req.getPassportSeries(),req.getPassportNumber(),null,null))")
    Client toClientFromLoan(LoanStatementRequestDto req);
}
