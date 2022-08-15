package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ADMIN;
import static com.scalar.ist.common.Constants.ADMINISTRATOR_ORGANIZATION;
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
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_SYSADMIN;
import static com.scalar.ist.common.Constants.ROLE_SYSOPERATOR;
import static com.scalar.ist.common.Constants.THIRD_PARTY_NAME;
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

import com.scalar.dl.ledger.crypto.CertificateEntry;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import com.scalar.ist.util.Util;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RegisterCompanyTest {
  private static final String MOCKED_ASSET_NAME = "company";
  private static final String SCHEMA_FILENAME = "register_company.json";
  private static final String MOCKED_EXECUTOR_COMPANY_ID = "service-operator.com";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_COMPANY_NAME = "株式会社Scalar";
  private static final String MOCKED_CORPORATE_NUMBER = "6010001188571";
  private static final String MOCKED_THIRD_PARTY_NAME = "Scalar, Inc.";
  private static final String MOCKED_ADDRESS = "Shinjuku, Tokyo";
  private static final String MOCKED_EMAIL = "mail@example.com";
  private static final JsonObject MOCKED_COMPANY_METADATA = prepareCompanyInformation();
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(Long.MAX_VALUE);
  private final JsonArray mockedOrganizationIds =
      Json.createArrayBuilder()
          .add(UUID.randomUUID().toString())
          .add(MOCKED_ORGANIZATION_ID)
          .build();
  @Mock private Ledger ledger;
  private CertificateEntry.Key certificateKey = new CertificateEntry.Key(MOCKED_HOLDER_ID, 1);
  private RegisterCompany registerCompany;

  private static JsonObject prepareCompanyInformation() {
    return Json.createObjectBuilder()
        .add(THIRD_PARTY_NAME, MOCKED_THIRD_PARTY_NAME)
        .add(COMPANY_ADDRESS, MOCKED_ADDRESS)
        .add(COMPANY_EMAIL, MOCKED_EMAIL)
        .build();
  }

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    registerCompany = spy(new RegisterCompany());
  }

  @Test
  public void invoke_AsSysAdmin_ProperArgumentsAndPropertiesGiven_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile(ROLE_SYSADMIN);
    JsonObject permissionValidationArgument = preparePermissionValidationArgument(userProfile);
    JsonObject putRecordArgument = preparePutRecordArgument(argument, properties);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(registerCompany)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    when(registerCompany.getCertificateKey()).thenReturn(certificateKey);
    doReturn(userProfile)
        .when(registerCompany)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(registerCompany)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(registerCompany)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);

    // Act
    registerCompany.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(registerCompany).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(registerCompany).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(registerCompany).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(registerCompany)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(registerCompany, times(4)).invokeSubContract(any(), any(), any());
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
              registerCompany.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(registerCompany, never()).invokeSubContract(any(), any(), any());
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
              registerCompany.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(registerCompany, never()).invokeSubContract(any(), any(), any());
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
              registerCompany.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NAME_IS_MISSING);
    verify(registerCompany, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_AsOpAdmin_ProperArgumentsAndPropertiesGiven_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile(ROLE_SYSOPERATOR);
    JsonObject permissionValidationArgument = preparePermissionValidationArgument(userProfile);
    JsonObject putRecordArgument = preparePutRecordArgument(argument, properties);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(registerCompany)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    when(registerCompany.getCertificateKey()).thenReturn(certificateKey);
    doReturn(userProfile)
        .when(registerCompany)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(registerCompany)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(registerCompany)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);

    // Act
    registerCompany.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(registerCompany).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(registerCompany).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(registerCompany).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(registerCompany)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(registerCompany, times(4)).invokeSubContract(any(), any(), any());
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
    when(registerCompany.getCertificateKey()).thenReturn(certificateKey);
    JsonObject validateArgumentArgument =
        prepareValidationArgument(argument, propertiesWithWrongHolderId);
    doReturn(null)
        .when(registerCompany)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              registerCompany.invoke(ledger, argument, Optional.of(propertiesWithWrongHolderId));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_NOT_MATCHED);
    verify(registerCompany).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(registerCompany).invokeSubContract(any(), any(), any());
  }

  private JsonObject preparePutRecordArgument(JsonObject argument, JsonObject properties) {
    JsonNumber createdAt = argument.getJsonNumber(CREATED_AT);
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId = String.format("%s-%s", assetName, argument.getString(COMPANY_ID));

    JsonObject administratorOrganization =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, argument.getString(ORGANIZATION_ID))
            .add(ORGANIZATION_NAME, ADMIN)
            .add(ORGANIZATION_DESCRIPTION, ADMINISTRATOR_ORGANIZATION)
            .add(IS_ACTIVE, true)
            .build();
    JsonArray organizations = Json.createArrayBuilder().add(administratorOrganization).build();
    JsonObject data =
        Json.createObjectBuilder()
            .add(COMPANY_ID, argument.getString(COMPANY_ID))
            .add(COMPANY_NAME, argument.getString(COMPANY_NAME))
            .add(CORPORATE_NUMBER, argument.getString(CORPORATE_NUMBER))
            .add(COMPANY_METADATA, argument.getJsonObject(COMPANY_METADATA))
            .add(ORGANIZATIONS, organizations)
            .build();
    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
        .build();
  }

  private JsonObject prepareArgument() {
    return Json.createObjectBuilder()
        .add(EXECUTOR_COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID)
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(COMPANY_NAME, MOCKED_COMPANY_NAME)
        .add(CORPORATE_NUMBER, MOCKED_CORPORATE_NUMBER)
        .add(COMPANY_METADATA, MOCKED_COMPANY_METADATA)
        .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
        .add(CREATED_AT, MOCKED_CREATED_AT)
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

  private JsonObject preparePermissionValidationArgument(JsonObject userProfile) {
    JsonArray ROLES = Json.createArrayBuilder().add(ROLE_SYSADMIN).add(ROLE_SYSOPERATOR).build();
    return Json.createObjectBuilder()
        .add(ROLES_REQUIRED, ROLES)
        .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
        .build();
  }

  private JsonObject prepareUserProfile(String role) {
    JsonArray roles = Json.createArrayBuilder().add(role).build();
    return Json.createObjectBuilder()
        .add(ORGANIZATION_IDS, mockedOrganizationIds)
        .add(ROLES, roles)
        .build();
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID).build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }
}
