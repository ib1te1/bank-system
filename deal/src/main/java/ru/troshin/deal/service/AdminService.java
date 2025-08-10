package ru.troshin.deal.service;

import ru.troshin.deal.dto.StatementDto;

import java.util.List;

public interface AdminService {
    List<StatementDto> findAll(Integer limit, Integer offset);
    StatementDto findById(String statementId);
}
