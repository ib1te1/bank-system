# ScoringDataDto
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **amount** | **BigDecimal** | Запрошенная сумма кредита, ≥ 20000 | [default to null] |
| **term** | **Integer** | Срок кредита в месяцах, ≥ 6 | [default to null] |
| **gender** | [**Gender**](Gender.md) |  | [default to null] |
| **passportIssueDate** | **date** | Дата выдачи паспорта | [default to null] |
| **passportIssueBranch** | **String** | Подразделение, выдавшее паспорт | [default to null] |
| **maritalStatus** | [**MaritalStatus**](MaritalStatus.md) |  | [default to null] |
| **dependentAmount** | **Integer** | Количество иждивенцев | [default to null] |
| **employment** | [**EmploymentDto**](EmploymentDto.md) |  | [default to null] |
| **accountNumber** | **String** | Номер банковского счета | [default to null] |
| **isInsuranceEnabled** | **Boolean** |  | [default to null] |
| **isSalaryClient** | **Boolean** |  | [default to null] |
| **firstName** | **String** |  | [default to null] |
| **lastName** | **String** |  | [default to null] |
| **middleName** | **String** | Отчество(опционально) | [optional] [default to null] |
| **birthdate** | **date** |  | [default to null] |
| **passportSeries** | **String** |  | [default to null] |
| **passportNumber** | **String** |  | [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

