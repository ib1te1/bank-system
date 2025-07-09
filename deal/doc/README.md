# Documentation for Deal Service API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *DealApi* | [**dealCalculateStatementIdPost**](Apis/DealApi.md#dealcalculatestatementidpost) | **POST** /deal/calculate/{statementId} | Завершить регистрацию и рассчитать кредит |
*DealApi* | [**dealOfferSelectPost**](Apis/DealApi.md#dealofferselectpost) | **POST** /deal/offer/select | Выбрать одно из предложений |
*DealApi* | [**dealStatementPost**](Apis/DealApi.md#dealstatementpost) | **POST** /deal/statement | Создать заявку и получить кредитные предложения |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [ApplicationStatus](./Models/ApplicationStatus.md)
 - [ChangeType](./Models/ChangeType.md)
 - [ClientError](./Models/ClientError.md)
 - [CreditDto](./Models/CreditDto.md)
 - [EmploymentDto](./Models/EmploymentDto.md)
 - [EmploymentPosition](./Models/EmploymentPosition.md)
 - [EmploymentStatus](./Models/EmploymentStatus.md)
 - [FinishRegistrationRequestDto](./Models/FinishRegistrationRequestDto.md)
 - [Gender](./Models/Gender.md)
 - [LoanOfferDto](./Models/LoanOfferDto.md)
 - [LoanStatementRequestDto](./Models/LoanStatementRequestDto.md)
 - [MaritalStatus](./Models/MaritalStatus.md)
 - [PaymentScheduleElementDto](./Models/PaymentScheduleElementDto.md)
 - [PersonInfoDto](./Models/PersonInfoDto.md)
 - [ScoringDataDto](./Models/ScoringDataDto.md)
 - [ServerError](./Models/ServerError.md)
 - [StatementStatusHistoryDto](./Models/StatementStatusHistoryDto.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
