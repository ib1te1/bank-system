package ru.troshin.deal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.troshin.deal.dto.StatementDto;
import ru.troshin.deal.exception.NoSuchStatementException;
import ru.troshin.deal.service.AdminService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController implements AdminApi {

    private final AdminService adminService;

    @Override
    public ResponseEntity<List<StatementDto>> dealAdminStatementGet(Integer limit, Integer offset){
        log.info("HTTP GET /deal/admin/statement called with limit={} offset={}", limit, offset);
        List<StatementDto> list = adminService.findAll(limit, offset);
        log.info("HTTP GET /deal/admin/statement returned {} items", list.size());
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<StatementDto> dealAdminStatementStatementIdGet(String statementId){
        log.info("HTTP GET /deal/admin/statement/{} called", statementId);
        try {
            StatementDto dto = adminService.findById(statementId);
            log.info("HTTP GET /deal/admin/statement/{} succeeded ", statementId);
            return ResponseEntity.ok(dto);
        } catch (NoSuchStatementException nsse) {
            log.warn("HTTP GET /deal/admin/statement/{} not found", statementId);
            throw nsse;
        } catch (Exception ex) {
            log.error("HTTP GET /deal/admin/statement/{} failed", statementId, ex);
            throw ex;
        }
    }
}
