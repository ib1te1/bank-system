package ru.troshin.deal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import ru.troshin.deal.calculator.dto.ScoringDataDto;
import ru.troshin.deal.dto.LoanOfferDto;
import ru.troshin.deal.entity.Client;

@Mapper(componentModel = "spring", uses = { EmploymentMapper.class })
public interface ScoringMapper {

    ScoringDataDto toCalcScoring(ru.troshin.deal.dto.ScoringDataDto dealDto);

    @Mappings({
            @Mapping(target = "firstName",          source = "firstName"),
            @Mapping(target = "lastName",           source = "lastName"),
            @Mapping(target = "middleName",         source = "middleName"),
            @Mapping(target = "gender",             source = "gender"),
            @Mapping(target = "birthdate",          source = "birthDate"),
            @Mapping(target = "passportSeries",     source = "passport.series"),
            @Mapping(target = "passportNumber",     source = "passport.number"),
            @Mapping(target = "passportIssueDate",  source = "passport.issueDate"),
            @Mapping(target = "passportIssueBranch",source = "passport.issueBranch"),
            @Mapping(target = "maritalStatus",      source = "maritalStatus"),
            @Mapping(target = "dependentAmount",    source = "dependentAmount"),
            @Mapping(target = "employment",         source = "employment"),
            @Mapping(target = "accountNumber",      source = "accountNumber")
    })
    ru.troshin.deal.dto.ScoringDataDto toDealScoring(Client client);

    @Mappings({
            @Mapping(target = "amount",               source = "requestedAmount"),
            @Mapping(target = "term",                 source = "term"),
            @Mapping(target = "isInsuranceEnabled",   source = "isInsuranceEnabled"),
            @Mapping(target = "isSalaryClient",       source = "isSalaryClient")
    })
    void updateScoring(LoanOfferDto offer, @MappingTarget ru.troshin.deal.dto.ScoringDataDto scoring);
}
