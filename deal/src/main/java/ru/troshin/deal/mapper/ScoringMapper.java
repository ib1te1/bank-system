package ru.troshin.deal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.troshin.deal.calculator.dto.ScoringDataDto;
import ru.troshin.deal.dto.LoanOfferDto;
import ru.troshin.deal.entity.Client;

@Mapper(componentModel = "spring", uses = { EmploymentMapper.class })
public interface ScoringMapper {

    ScoringDataDto toCalcScoring(ru.troshin.deal.dto.ScoringDataDto dealDto);

    @Mappings({
            @Mapping(target = "amount",               source = "offer.requestedAmount"),
            @Mapping(target = "term",                 source = "offer.term"),
            @Mapping(target = "firstName",            source = "client.firstName"),
            @Mapping(target = "lastName",             source = "client.lastName"),
            @Mapping(target = "middleName",           source = "client.middleName"),
            @Mapping(target = "gender",               source = "client.gender"),
            @Mapping(target = "birthdate",            source = "client.birthDate"),
            @Mapping(target = "passportSeries",       source = "client.passport.series"),
            @Mapping(target = "passportNumber",       source = "client.passport.number"),
            @Mapping(target = "passportIssueDate",    source = "client.passport.issueDate"),
            @Mapping(target = "passportIssueBranch",  source = "client.passport.issueBranch"),
            @Mapping(target = "maritalStatus",        source = "client.maritalStatus"),
            @Mapping(target = "dependentAmount",      source = "client.dependentAmount"),
            @Mapping(target = "employment",           source = "client.employment"),
            @Mapping(target = "accountNumber",        source = "client.accountNumber"),
            @Mapping(target = "isInsuranceEnabled",   source = "offer.isInsuranceEnabled"),
            @Mapping(target = "isSalaryClient",       source = "offer.isSalaryClient")
    })
    ru.troshin.deal.dto.ScoringDataDto toDealScoring(Client client, LoanOfferDto offer);
}
