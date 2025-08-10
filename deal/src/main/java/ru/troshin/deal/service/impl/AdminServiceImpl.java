package ru.troshin.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.troshin.deal.dto.StatementDto;
import ru.troshin.deal.exception.NoSuchStatementException;
import ru.troshin.deal.mapper.StatementMapper;
import ru.troshin.deal.repository.StatementRepository;
import ru.troshin.deal.service.AdminService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminServiceImpl implements AdminService {

    private final StatementRepository statementRepository;
    private final StatementMapper statementMapper;


    @Override
    public List<StatementDto> findAll(Integer limit, Integer offset) {
        log.info("AdminService.findAll called with limit={} offset={}", limit, offset);
        int safeLimit = (limit == null || limit <= 0) ? 10 : limit;
        int safeOffset = (offset == null || offset < 0) ? 0 : offset;
        int page = safeOffset / safeLimit;
        Pageable pageable = PageRequest.of(page, safeLimit);
        log.debug("Computed paging: safeLimit={}, safeOffset={}, page={}, pageable={}", safeLimit, safeOffset, page, pageable);
        try {
            var pageResult = statementRepository.findAll(pageable);
            List<StatementDto> list = pageResult
                    .stream()
                    .map(statementMapper::toStatementDto)
                    .collect(Collectors.toList());

            log.info("AdminService.findAll completed: returned {} statements", list.size());
            log.debug("Statements ids returned: {}", list.stream().map(StatementDto::getId).toList());

            return list;
        } catch (Exception ex) {
            log.error("AdminService.findAll failed (limit={}, offset={})", limit, offset, ex);
            throw ex;
        }
    }

    @Override
    public StatementDto findById(String statementId) {
        log.info("AdminService.findById called with statementId={}", statementId);
        UUID uuid;
        try {
            uuid = UUID.fromString(statementId);
            log.debug("Parsed statementId to UUID={}", uuid);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid UUID format for statementId={}", statementId, ex);
            throw new NoSuchStatementException("Invalid statement id format: " + statementId);
        }

        try {
            var opt = statementRepository.findById(uuid);
            if (opt.isEmpty()) {
                log.warn("Statement not found for id={}", uuid);
                throw new NoSuchStatementException("Statement not found: " + statementId);
            }
            StatementDto dto = statementMapper.toStatementDto(opt.get());
            log.info("AdminService.findById completed for id={}", uuid);
            log.debug("Found statement dto: {}", dto);
            return dto;
        } catch (NoSuchStatementException e) {
            throw e;
        } catch (Exception ex) {
            log.error("AdminService.findById unexpected error for id={}", statementId, ex);
            throw ex;
        }
    }
}
