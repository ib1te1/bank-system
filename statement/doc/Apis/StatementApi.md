# StatementApi

All URIs are relative to *http://localhost:8082*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**statementOfferPost**](StatementApi.md#statementOfferPost) | **POST** /statement/offer | Выбор одного из предложений. |
| [**statementPost**](StatementApi.md#statementPost) | **POST** /statement | Прескоринг + запрос на расчёт возможных условий кредита. |


<a name="statementOfferPost"></a>
# **statementOfferPost**
> statementOfferPost(LoanOfferDto)

Выбор одного из предложений.

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

<a name="statementPost"></a>
# **statementPost**
> List statementPost(LoanStatementRequestDto)

Прескоринг + запрос на расчёт возможных условий кредита.

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

