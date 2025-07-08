package ru.troshin.deal.mapper;

import org.mapstruct.Mapper;
import ru.troshin.deal.calculator.dto.ScoringDataDto;

@Mapper(componentModel = "spring")
public interface ScoringMapper {

    ScoringDataDto toCalcScoring(ru.troshin.deal.dto.ScoringDataDto dealDto);
}
