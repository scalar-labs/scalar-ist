package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ADMIN;
import static com.scalar.ist.common.Constants.ADMINISTRATOR_ORGANIZATION;
import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.COMPANY_ADDRESS;
import static com.scalar.ist.common.Constants.COMPANY_ASSET_NAME;
import static com.scalar.ist.common.Constants.COMPANY_ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_EMAIL;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.COMPANY_METADATA;
import static com.scalar.ist.common.Constants.COMPANY_NAME;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.EXECUTION_RESTRICTED_TO_INITIALIZER_ACCOUNT;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_SYSADMIN;
import static com.scalar.ist.common.Constants.HOLDER_ID_SYSOPERATOR;
import static com.scalar.ist.common.Constants.INITIALIZER_ACCOUNT_NAME;
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
import static com.scalar.ist.common.Constants.ROLE_SYSADMIN;
import static com.scalar.ist.common.Constants.ROLE_SYSOPERATOR;
import static com.scalar.ist.common.Constants.THIRD_PARTY_NAME;
import static com.scalar.ist.common.Constants.USER_PROFILE_ASSET_NAME;
import static com.scalar.ist.common.Constants.USER_PROFILE_ASSET_VERSION;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.dl.ledger.crypto.CertificateEntry.Key;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.util.Util;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InitializeTest {
  private static final String MOCKED_COMPANY_ASSET_NAME = "company";
  private static final String MOCKED_USER_PROFILE_ASSET_NAME = "user_profile";
  private static final String SCHEMA_FILENAME = "initialize.json";
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
  private static final String MOCKED_SYSADMIN = UUID.randomUUID().toString();
  private static final String MOCKED_SYSOPERATOR = UUID.randomUUID().toString();
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(Long.MAX_VALUE);
  @Mock private Ledger ledger;
  @Mock private Key certificateKey;
  private Initialize initialize;

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
    initialize = spy(new Initialize());
  }

  @Test
  public void invoke_ProperArgumentsAndPropertiesGiven_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject putRecordArgument = preparePutRecordArgument(argument, properties);
    JsonObject putRecordArgumentSysAdminArgument =
        preparePutUserProfileArgument(
            argument, properties, ROLE_SYSADMIN, argument.getString(HOLDER_ID_SYSADMIN));
    JsonObject putRecordArgumentOpAdminArgument =
        preparePutUserProfileArgument(
            argument, properties, ROLE_SYSOPERATOR, argument.getString(HOLDER_ID_SYSOPERATOR));
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(initialize.getCertificateKey()).thenReturn(certificateKey);
    when(initialize.getCertificateKey().getHolderId()).thenReturn(INITIALIZER_ACCOUNT_NAME);
    doReturn(null)
        .when(initialize)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(initialize)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    doReturn(null)
        .when(initialize)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgumentSysAdminArgument);
    doReturn(null)
        .when(initialize)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgumentOpAdminArgument);

    // Act
    initialize.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(initialize).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(initialize).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(initialize)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgumentSysAdminArgument);
    verify(initialize)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgumentOpAdminArgument);
    verify(initialize, times(4)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithoutProperty_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    when(initialize.getCertificateKey()).thenReturn(certificateKey);
    when(initialize.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              initialize.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(initialize, never()).invokeSubContract(any(), any(), any());
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
            .add(COMPANY_ASSET_NAME, MOCKED_COMPANY_ASSET_NAME)
            .add(USER_PROFILE_ASSET_NAME, MOCKED_USER_PROFILE_ASSET_NAME)
            .build();
    JsonObject validateArgumentArgument =
        prepareValidationArgument(argument, propertiesWithWrongHolderId);
    when(initialize.getCertificateKey()).thenReturn(certificateKey);
    when(initialize.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(null)
        .when(initialize)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              initialize.invoke(ledger, argument, Optional.of(propertiesWithWrongHolderId));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(EXECUTION_RESTRICTED_TO_INITIALIZER_ACCOUNT);
    verify(initialize).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(initialize).invokeSubContract(any(), any(), any());
  }

  private JsonObject preparePutRecordArgument(JsonObject argument, JsonObject properties) {
    JsonNumber createdAt = argument.getJsonNumber(CREATED_AT);
    String assetName =
        properties.getString(COMPANY_ASSET_NAME) + properties.getString(COMPANY_ASSET_VERSION, "");
    String assetId = String.format("%s-%s", assetName, argument.getString(COMPANY_ID));

    JsonObject administratorInformation =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, argument.getString(ORGANIZATION_ID))
            .add(ORGANIZATION_NAME, ADMIN)
            .add(ORGANIZATION_DESCRIPTION, ADMINISTRATOR_ORGANIZATION)
            .add(IS_ACTIVE, true)
            .build();
    JsonObject data =
        Json.createObjectBuilder()
            .add(COMPANY_ID, argument.getString(COMPANY_ID))
            .add(COMPANY_NAME, argument.getString(COMPANY_NAME))
            .add(CORPORATE_NUMBER, argument.getString(CORPORATE_NUMBER))
            .add(COMPANY_METADATA, argument.getJsonObject(COMPANY_METADATA))
            .add(ORGANIZATIONS, Json.createArrayBuilder().add(administratorInformation))
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
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(COMPANY_NAME, MOCKED_COMPANY_NAME)
        .add(CORPORATE_NUMBER, MOCKED_CORPORATE_NUMBER)
        .add(COMPANY_METADATA, MOCKED_COMPANY_METADATA)
        .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
        .add(HOLDER_ID_SYSADMIN, MOCKED_SYSADMIN)
        .add(HOLDER_ID_SYSOPERATOR, MOCKED_SYSOPERATOR)
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
        .add(HOLDER_ID, INITIALIZER_ACCOUNT_NAME)
        .add(COMPANY_ASSET_NAME, MOCKED_COMPANY_ASSET_NAME)
        .add(USER_PROFILE_ASSET_NAME, MOCKED_USER_PROFILE_ASSET_NAME)
        .build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }

  private JsonObject preparePutUserProfileArgument(
      JsonObject arguments, JsonObject properties, String role, String holderIdConstant) {
    String assetName =
        properties.getString(USER_PROFILE_ASSET_NAME)
            + properties.getString(USER_PROFILE_ASSET_VERSION, "");
    String assetId =
        String.format("%s-%s-%s", assetName, arguments.getString(COMPANY_ID), holderIdConstant);

    JsonObject data =
        Json.createObjectBuilder()
            .add(COMPANY_ID, arguments.getString(COMPANY_ID))
            .add(
                ORGANIZATION_IDS,
                Json.createArrayBuilder().add(arguments.getString(ORGANIZATION_ID)).build())
            .add(HOLDER_ID, holderIdConstant)
            .add(ROLES, Json.createArrayBuilder().add(role).build())
            .build();
    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, arguments.getJsonNumber(CREATED_AT))
        .build();
  }
}
