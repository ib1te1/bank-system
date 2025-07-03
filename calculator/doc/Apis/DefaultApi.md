# DefaultApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**calculatorCalcPost**](DefaultApi.md#calculatorCalcPost) | **POST** /calculator/calc | Валидация присланных данных, скоринг данных и полный расчет параметров кредита |
| [**calculatorOffersPost**](DefaultApi.md#calculatorOffersPost) | **POST** /calculator/offers | Расчёт возможных условий кредита |


<a name="calculatorCalcPost"></a>
# **calculatorCalcPost**
> CreditDto calculatorCalcPost(ScoringDataDto)

Валидация присланных данных, скоринг данных и полный расчет параметров кредита

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **ScoringDataDto** | [**ScoringDataDto**](../Models/ScoringDataDto.md)|  | |

### Return type

[**CreditDto**](../Models/CreditDto.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="calculatorOffersPost"></a>
# **calculatorOffersPost**
> List calculatorOffersPost(LoanStatementRequestDto)

Расчёт возможных условий кредита

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

