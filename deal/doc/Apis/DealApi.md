# DealApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**dealCalculateStatementIdPost**](DealApi.md#dealCalculateStatementIdPost) | **POST** /deal/calculate/{statementId} | Завершить регистрацию и рассчитать кредит |
| [**dealOfferSelectPost**](DealApi.md#dealOfferSelectPost) | **POST** /deal/offer/select | Выбрать одно из предложений |
| [**dealStatementPost**](DealApi.md#dealStatementPost) | **POST** /deal/statement | Создать заявку и получить кредитные предложения |


<a name="dealCalculateStatementIdPost"></a>
# **dealCalculateStatementIdPost**
> dealCalculateStatementIdPost(statementId, FinishRegistrationRequestDto)

Завершить регистрацию и рассчитать кредит

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **statementId** | **String**| UUID заявки | [default to null] |
| **FinishRegistrationRequestDto** | [**FinishRegistrationRequestDto**](../Models/FinishRegistrationRequestDto.md)|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="dealOfferSelectPost"></a>
# **dealOfferSelectPost**
> dealOfferSelectPost(LoanOfferDto)

Выбрать одно из предложений

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **LoanOfferDto** | [**LoanOfferDto**](../Models/LoanOfferDto.md)|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="dealStatementPost"></a>
# **dealStatementPost**
> List dealStatementPost(LoanStatementRequestDto)

Создать заявку и получить кредитные предложения

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **LoanStatementRequestDto** | [**LoanStatementRequestDto**](../Models/LoanStatementRequestDto.md)|  | |

### Return type

[**List**](../Models/LoanOfferDto.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

