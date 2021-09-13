package com.scalar.ist.contract;

import com.scalar.dl.ledger.crypto.CertificateEntry;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import com.scalar.ist.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import java.util.Optional;
import java.util.UUID;

import static com.scalar.ist.common.Constants.ADMIN;
import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ADDRESS;
import static com.scalar.ist.common.Constants.COMPANY_EMAIL;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.COMPANY_METADATA;
import static com.scalar.ist.common.Constants.COMPANY_NAME;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.EXECUTOR_COMPANY_ID;
import static com.scalar.ist.common.Constants.EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_NOT_MATCHED;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.ORGANIZATION_DESCRIPTION;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_NAME;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_UPSERT;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.ROLE_SYSOPERATOR;
import static com.scalar.ist.common.Constants.THIRD_PARTY_NAME;
import static com.scalar.ist.common.Constants.UPDATED_AT;
import static com.scalar.ist.common.Constants.USER_PROFILE_ROLES;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.VALIDATE_PERMISSION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateCompanyTest {
  private static final String MOCKED_ASSET_NAME = "company";
  private static final String SCHEMA_FILENAME = "update_company.json";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_EXECUTOR_COMPANY_ID = "service-operator.com";
  private static final String MOCKED_COMPANY_NAME = "株式会社Scalar";
  private static final String MOCKED_CORPORATE_NUMBER = "6010001188571";
  private static final JsonObject MOCKED_COMPANY_METADATA =
      Json.createObjectBuilder()
          .add(THIRD_PARTY_NAME, "Scalar, Inc")
          .add(COMPANY_ADDRESS, "Shinjuku, Tokyo")
          .add(COMPANY_EMAIL, "mail@example.com")
          .build();
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(Long.MAX_VALUE);
  private static final JsonNumber MOCKED_UPDATED_AT = Json.createValue(2);
  private final JsonArray mockedOrganizationIds =
      Json.createArrayBuilder()
          .add(UUID.randomUUID().toString())
          .add(MOCKED_ORGANIZATION_ID)
          .build();
  @Mock private Ledger ledger;
  @Mock private CertificateEntry.Key certificateKey;
  private UpdateCompany updateCompany;

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    updateCompany = spy(new UpdateCompany());
  }

  @Test
  public void invoke_ProperArgumentsAndPropertiesGivenFromAdministrator_ShouldPass() {
    JsonObject contractArgument =
        Json.createObjectBuilder(prepareArgument())
            .add(COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID)
            .build();
    invokeProperArgumentsWithGivenRole(ROLE_ADMINISTRATOR, contractArgument);
  }

  @Test
  public void invoke_ProperArgumentsAndPropertiesGivenFromSysOperator_ShouldPass() {
    invokeProperArgumentsWithGivenRole(ROLE_SYSOPERATOR, prepareArgument());
  }

  private void invokeProperArgumentsWithGivenRole(
      String executorRole, JsonObject contractArgument) {
    // Arrange
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile(executorRole);
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(userProfile, contractArgument);
    JsonObject companyRecord = prepareCompanyRecord(contractArgument);
    JsonObject putRecordArgument =
        preparePutRecordArgument(contractArgument, companyRecord, properties);
    JsonObject getRecordArgument = prepareGetRecordArgument(contractArgument, properties);
    JsonObject validateArgumentArgument = prepareValidationArgument(contractArgument, properties);
    when(updateCompany.getCertificateKey()).thenReturn(certificateKey);
    when(updateCompany.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(userProfile)
        .when(updateCompany)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(companyRecord)
        .when(updateCompany)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getRecordArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(updateCompany)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    doReturn(null)
        .when(updateCompany)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(null)
        .when(updateCompany)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    updateCompany.invoke(ledger, contractArgument, Optional.of(properties));

    // Assert
    verify(updateCompany).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(updateCompany).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(updateCompany).invokeSubContract(GET_ASSET_RECORD, ledger, getRecordArgument);
    verify(updateCompany).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(updateCompany)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(updateCompany, times(5)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutSchema_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = Json.createObjectBuilder().add(HOLDER_ID, MOCKED_HOLDER_ID).build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              updateCompany.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(updateCompany, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutHolderId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                    .build())
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              updateCompany.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(updateCompany, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutAssetName_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                    .build())
            .add(HOLDER_ID, MOCKED_HOLDER_ID)
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              updateCompany.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NAME_IS_MISSING);
    verify(updateCompany, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithWrongHolderId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject propertiesWithWrongHolderId =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                    .build())
            .add(HOLDER_ID, UUID.randomUUID().toString())
            .add(ASSET_NAME, MOCKED_ASSET_NAME)
            .build();
    JsonObject validateArgumentArgument =
        prepareValidationArgument(argument, propertiesWithWrongHolderId);
    doReturn(null)
        .when(updateCompany)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    when(updateCompany.getCertificateKey()).thenReturn(certificateKey);
    when(updateCompany.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);

    // Act
    // Assert
    assertThatThrownBy(
            () -> updateCompany.invoke(ledger, argument, Optional.of(propertiesWithWrongHolderId)))
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_NOT_MATCHED);
    verify(updateCompany).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(updateCompany).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_AdminRoleWithNonMatchingCompanyId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile =
        Json.createObjectBuilder()
            .add(COMPANY_ID, "wrong-company.com")
            .add(ORGANIZATION_IDS, mockedOrganizationIds)
            .add(ROLES, Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build())
            .build();
    JsonObject companyRecord = prepareCompanyRecord(argument);
    JsonObject putRecordArgument = preparePutRecordArgument(argument, companyRecord, properties);
    JsonObject getRecordArgument = prepareGetRecordArgument(argument, properties);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(updateCompany.getCertificateKey()).thenReturn(certificateKey);
    when(updateCompany.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(userProfile)
        .when(updateCompany)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(companyRecord)
        .when(updateCompany)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getRecordArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(updateCompany)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    doReturn(null)
        .when(updateCompany)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    assertThatThrownBy(() -> updateCompany.invoke(ledger, argument, Optional.of(properties)))
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID);
    verify(updateCompany).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(updateCompany).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(updateCompany, times(2)).invokeSubContract(any(), any(), any());
  }

  private JsonObject preparePutRecordArgument(
      JsonObject argument, JsonObject companyRecord, JsonObject properties) {
    JsonNumber updatedAt = argument.getJsonNumber(UPDATED_AT);
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId = String.format("%s-%s", assetName, argument.getString(COMPANY_ID));

    JsonObject data =
        Json.createObjectBuilder()
            .add(COMPANY_ID, argument.getString(COMPANY_ID))
            .add(COMPANY_NAME, argument.getString(COMPANY_NAME))
            .add(CORPORATE_NUMBER, argument.getString(CORPORATE_NUMBER))
            .add(COMPANY_METADATA, argument.getJsonObject(COMPANY_METADATA))
            .add(ORGANIZATIONS, companyRecord.get(ORGANIZATIONS))
            .build();
    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_MODE, RECORD_MODE_UPSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, updatedAt)
        .build();
  }

  private JsonObject prepareGetRecordArgument(JsonObject argument, JsonObject properties) {
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId = String.format("%s-%s", assetName, argument.getString(COMPANY_ID));
    return Json.createObjectBuilder().add(ASSET_ID, assetId).add(RECORD_IS_HASHED, false).build();
  }

  private JsonObject prepareCompanyRecord(JsonObject argument) {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, argument.getString(COMPANY_ID))
        .add(COMPANY_NAME, "Acme")
        .add(CORPORATE_NUMBER, "6010501188571")
        .add(COMPANY_METADATA, Json.createObjectBuilder().add(THIRD_PARTY_NAME, "Acme, Inc"))
        .add(COMPANY_ADDRESS, "San Francisco, California")
        .add(COMPANY_EMAIL, "contact@acme.com")
        .add(
            ORGANIZATIONS,
            Json.createArrayBuilder()
                .add(
                    Json.createObjectBuilder()
                        .add(ORGANIZATION_ID, UUID.randomUUID().toString())
                        .add(ORGANIZATION_NAME, ADMIN)
                        .add(ORGANIZATION_DESCRIPTION, "My organization")
                        .add(IS_ACTIVE, true)
                        .build()))
        .build();
  }

  private JsonObject prepareArgument() {
    return Json.createObjectBuilder()
        .add(EXECUTOR_COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID)
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(COMPANY_NAME, MOCKED_COMPANY_NAME)
        .add(CORPORATE_NUMBER, MOCKED_CORPORATE_NUMBER)
        .add(COMPANY_METADATA, MOCKED_COMPANY_METADATA)
        .add(CREATED_AT, MOCKED_CREATED_AT)
        .add(UPDATED_AT, MOCKED_UPDATED_AT)
        .build();
  }

  private JsonObject prepareProperties() {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                .build())
        .add(HOLDER_ID, MOCKED_HOLDER_ID)
        .add(ASSET_NAME, MOCKED_ASSET_NAME)
        .build();
  }

  private JsonObject prepareUserProfile(String role) {
    JsonArray roles = Json.createArrayBuilder().add(role).build();
    return Json.createObjectBuilder()
        .add(COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID)
        .add(ORGANIZATION_IDS, mockedOrganizationIds)
        .add(ROLES, roles)
        .build();
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID).build();
  }

  private JsonObject preparePermissionValidationArgument(
      JsonObject userProfile, JsonObject arguments) {
    JsonArray ROLES =
        Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).add(ROLE_SYSOPERATOR).build();
    return Json.createObjectBuilder()
        .add(ROLES_REQUIRED, ROLES)
        .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
        .build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }
}
