package ru.troshin.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import ru.troshin.gateway.deal.controller.AdminApi;
import ru.troshin.gateway.deal.controller.DealApi;
import ru.troshin.gateway.deal.dto.FinishRegistrationRequestDto;
import ru.troshin.gateway.deal.dto.StatementDto;
import ru.troshin.gateway.exception.DownstreamException;
import ru.troshin.gateway.statement.controller.StatementApi;
import ru.troshin.gateway.statement.dto.LoanOfferDto;
import ru.troshin.gateway.statement.dto.LoanStatementRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GatewayController {

    private final StatementApi statementApi;
    private final DealApi dealApi;
    private final AdminApi adminApi;


    @Operation(summary = "Прескоринг + запрос на расчёт возможных условий кредита (statement)")
    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> statementPost(@RequestBody LoanStatementRequestDto req){
        log.info("Gateway: POST /statement incoming, body={}", req);
        try {
            log.debug("Gateway calling Statement service: POST /statement");
            List<LoanOfferDto> offers=statementApi.statementPost(req).getBody();
            return ResponseEntity.ok(offers);
        }
        catch (RestClientResponseException rcre) {
            throw new DownstreamException("statement", rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString(), "Downstream returned error", rcre);
        } catch (ResourceAccessException rae) {
            throw new DownstreamException("statement", null, null,
                    "Network error contacting statement service", rae);
        } catch (RestClientException rce) {
            throw new DownstreamException("statement", null, null,
                    "Error contacting statement service", rce);
        }
    }
    @Operation(summary = "Выбор офера (statement -> deal)")
    @PostMapping("/statement/offer")
    public ResponseEntity<Void> statementOfferPost(@RequestBody LoanOfferDto loanOfferDto){
        log.info("Gateway: POST /statement/offer incoming, body={}", loanOfferDto);
        try {
            log.debug("Gateway calling Statement service: POST /statement/offer");
            statementApi.statementOfferPost(loanOfferDto);
            log.info("Gateway: forwarded offer selection to Statement service");
            return ResponseEntity.noContent().build();
        } catch (RestClientResponseException rcre) {
            throw new DownstreamException("statement", rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString(), "Downstream returned error", rcre);
        } catch (ResourceAccessException rae) {
            throw new DownstreamException("statement", null, null,
                    "Network error contacting statement service", rae);
        } catch (RestClientException rce) {
            throw new DownstreamException("statement", null, null,
                    "Error contacting statement service", rce);
        }
    }


    @Operation(summary = "Создать заявку и получить кредитные предложения (/deal/statement)")
    @PostMapping("/deal/statement")
    public ResponseEntity<List<ru.troshin.gateway.deal.dto.LoanOfferDto>> dealStatementPost(
            @RequestBody ru.troshin.gateway.deal.dto.LoanStatementRequestDto loanStatementRequestDto
    ){
        log.info("Gateway: POST /deal/statement incoming, body={}", loanStatementRequestDto);
        try {
            log.debug("Gateway calling Deal service: POST /deal/statement");
            var offers=dealApi.dealStatementPost(loanStatementRequestDto).getBody();
            return ResponseEntity.ok(offers);
        }
        catch (RestClientResponseException rcre) {
            throw new DownstreamException("deal", rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString(), "Downstream returned error", rcre);
        } catch (ResourceAccessException rae) {
            throw new DownstreamException("deal", null, null,
                    "Network error contacting deal service", rae);
        } catch (RestClientException rce) {
            throw new DownstreamException("deal", null, null,
                    "Error contacting deal service", rce);
        }
    }
    @Operation(summary = "Выбрать одно из предложений (/deal/offer/select)")
    @PostMapping("/deal/offer/select")
    ResponseEntity<Void> dealOfferSelectPost(
            @RequestBody ru.troshin.gateway.deal.dto.LoanOfferDto loanOfferDto
    ){
        log.info("Gateway: POST /deal/offer/select incoming, body={}",loanOfferDto);
        try {
            log.debug("Gateway calling Deal service: POST /deal/offer/select");
            dealApi.dealOfferSelectPost(loanOfferDto);
            return ResponseEntity.noContent().build();
        }
        catch (RestClientResponseException rcre) {
            throw new DownstreamException("deal", rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString(), "Downstream returned error", rcre);
        } catch (ResourceAccessException rae) {
            throw new DownstreamException("deal", null, null,
                    "Network error contacting deal service", rae);
        } catch (RestClientException rce) {
            throw new DownstreamException("deal", null, null,
                    "Error contacting deal service", rce);
        }
    }
    @Operation(summary = "Завершить регистрацию и рассчитать кредит (/deal/calculate/{statementId})")
    @PostMapping("/deal/calculate/{statementId}")
    public ResponseEntity<Void> calculate(@PathVariable String statementId,
                                          @RequestBody FinishRegistrationRequestDto dto) {
        log.info("Gateway: POST /deal/calculate/{} incoming, body={}", statementId, dto);
        try {
            log.debug("Gateway calling Deal service: POST /deal/calculate/{}", statementId);
            dealApi.dealCalculateStatementIdPost(statementId, dto);
            return ResponseEntity.noContent().build();
        }
        catch (RestClientResponseException rcre) {
            throw new DownstreamException("deal", rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString(), "Downstream returned error", rcre);
        } catch (ResourceAccessException rae) {
            throw new DownstreamException("deal", null, null,
                    "Network error contacting deal service", rae);
        } catch (RestClientException rce) {
            throw new DownstreamException("deal", null, null,
                    "Error contacting deal service", rce);
        }
    }

    @Operation(summary = "Запрос на отправку документов клиенту (/deal/document/{statementId}/send)")
    @PostMapping("/deal/document/{statementId}/send")
    public ResponseEntity<Void> sendDocuments(@PathVariable String statementId) {
        log.info("Gateway: POST /deal/document/{}/send incoming", statementId);
        try {
            log.debug("Gateway calling Deal service: POST /deal/document/{}/send", statementId);
            dealApi.dealDocumentStatementIdSendPost(statementId);
            return ResponseEntity.accepted().build();
        }
        catch (RestClientResponseException rcre) {
            throw new DownstreamException("deal", rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString(), "Downstream returned error", rcre);
        } catch (ResourceAccessException rae) {
            throw new DownstreamException("deal", null, null,
                    "Network error contacting deal service", rae);
        } catch (RestClientException rce) {
            throw new DownstreamException("deal", null, null,
                    "Error contacting deal service", rce);
        }
    }

    @Operation(summary = "Запрос на подписание документов (отправка кода SES) (/deal/document/{statementId}/sign)")
    @PostMapping("/deal/document/{statementId}/sign")
    public ResponseEntity<Void> signDocuments(@PathVariable String statementId) {
        log.info("Gateway: POST /deal/document/{}/sign incoming", statementId);
        try {
            log.debug("Gateway calling Deal service: POST /deal/document/{}/sign", statementId);
            dealApi.dealDocumentStatementIdSignPost(statementId);
            return ResponseEntity.accepted().build();
        }
        catch (RestClientResponseException rcre) {
            throw new DownstreamException("deal", rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString(), "Downstream returned error", rcre);
        } catch (ResourceAccessException rae) {
            throw new DownstreamException("deal", null, null,
                    "Network error contacting deal service", rae);
        } catch (RestClientException rce) {
            throw new DownstreamException("deal", null, null,
                    "Error contacting deal service", rce);
        }
    }

    @Operation(summary = "Подтверждение кода для подписания документов и завершение подписи (/deal/document/{statementId}/code)")
    @PostMapping("/deal/document/{statementId}/code")
    public ResponseEntity<Void> verifyDocumentCode(@PathVariable String statementId) {
        log.info("Gateway: POST /deal/document/{}/code incoming", statementId);
        try {
            log.debug("Gateway calling Deal service: POST /deal/document/{}/code", statementId);
            dealApi.dealDocumentStatementIdCodePost(statementId);
            return ResponseEntity.accepted().build();
        }
        catch (RestClientResponseException rcre) {
            throw new DownstreamException("deal", rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString(), "Downstream returned error", rcre);
        } catch (ResourceAccessException rae) {
            throw new DownstreamException("deal", null, null,
                    "Network error contacting deal service", rae);
        } catch (RestClientException rce) {
            throw new DownstreamException("deal", null, null,
                    "Error contacting deal service", rce);
        }
    }


    @Operation(summary = "Админ: получить все заявки (/deal/admin/statement)")
    @GetMapping("/deal/admin/statement")
    public ResponseEntity<List<StatementDto>> adminFindAll(@RequestParam(required = false) Integer limit,
                                                           @RequestParam(required = false) Integer offset) {
        log.info("Gateway: GET /deal/admin/statement incoming, limit={}, offset={}", limit, offset);
        try {
            log.debug("Gateway calling Deal service: GET /deal/admin/statement");
            var resp = adminApi.dealAdminStatementGet(limit, offset);
            List<StatementDto> list = resp.getBody();
            log.info("Gateway: Deal admin returned {} statements", list == null ? 0 : list.size());
            return ResponseEntity.ok(list);
        }
        catch (RestClientResponseException rcre) {
            throw new DownstreamException("deal", rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString(), "Downstream returned error", rcre);
        } catch (ResourceAccessException rae) {
            throw new DownstreamException("deal", null, null,
                    "Network error contacting deal service", rae);
        } catch (RestClientException rce) {
            throw new DownstreamException("deal", null, null,
                    "Error contacting deal service", rce);
        }
    }

    @Operation(summary = "Админ: получить заявку по id (/deal/admin/statement/{statementId})")
    @GetMapping("/deal/admin/statement/{statementId}")
    public ResponseEntity<StatementDto> adminFindById(@PathVariable String statementId) {
        log.info("Gateway: GET /deal/admin/statement/{} incoming", statementId);
        try {
            log.debug("Gateway calling Deal service: GET /deal/admin/statement/{}", statementId);
            var resp = adminApi.dealAdminStatementStatementIdGet(statementId);
            StatementDto dto = resp.getBody();
            log.info("Gateway: Deal admin returned statement id={}", statementId);
            return ResponseEntity.ok(dto);
        }
        catch (RestClientResponseException rcre) {
            throw new DownstreamException("deal", rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString(), "Downstream returned error", rcre);
        } catch (ResourceAccessException rae) {
            throw new DownstreamException("deal", null, null,
                    "Network error contacting deal service", rae);
        } catch (RestClientException rce) {
            throw new DownstreamException("deal", null, null,
                    "Error contacting deal service", rce);
        }
    }

}
