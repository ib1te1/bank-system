# Documentation for Calculator Service API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *DefaultApi* | [**calculatorCalcPost**](Apis/DefaultApi.md#calculatorcalcpost) | **POST** /calculator/calc | Валидация присланных данных, скоринг данных и полный расчет параметров кредита |
*DefaultApi* | [**calculatorOffersPost**](Apis/DefaultApi.md#calculatorofferspost) | **POST** /calculator/offers | Расчёт возможных условий кредита |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [ClientError](./Models/ClientError.md)
 - [CreditDto](./Models/CreditDto.md)
 - [EmploymentDto](./Models/EmploymentDto.md)
 - [EmploymentStatus](./Models/EmploymentStatus.md)
 - [Gender](./Models/Gender.md)
 - [LoanOfferDto](./Models/LoanOfferDto.md)
 - [LoanStatementRequestDto](./Models/LoanStatementRequestDto.md)
 - [MaritalStatus](./Models/MaritalStatus.md)
 - [PaymentScheduleElementDto](./Models/PaymentScheduleElementDto.md)
 - [PersonInfoDto](./Models/PersonInfoDto.md)
 - [Position](./Models/Position.md)
 - [ScoringDataDto](./Models/ScoringDataDto.md)
 - [ServerError](./Models/ServerError.md)
 - [UnexpectedError](./Models/UnexpectedError.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
