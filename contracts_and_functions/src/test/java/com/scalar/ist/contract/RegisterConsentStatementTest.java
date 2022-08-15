package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ABSTRACT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_BENEFIT_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DRAFT_STATUS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_OPTIONAL_PURPOSES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_PURPOSE_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_STATUS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_THIRD_PARTY_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_TITLE;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_VERSION;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.GROUP_COMPANY_IDS;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_NOT_MATCHED;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
import static com.scalar.ist.common.Constants.THIRD_PARTY_DESCRIPTION;
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

import com.scalar.dl.ledger.crypto.CertificateEntry.Key;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import com.scalar.ist.util.Hasher;
import com.scalar.ist.util.Util;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RegisterConsentStatementTest {
  private static final String SCHEMA_FILENAME = "register_consent_statement.json";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_ASSET_NAME = "mocked asset name";
  private static final String MOCKED_ASSET_VERSION = "mocked asset version";
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final String MOCKED_ABSTRACT = "Abstract of ConsentStatement";
  private static final String MOCKED_TITLE = "Title of ConsentStatement";
  private static final String MOCKED_DESCRIPTION = "Description of ConsentStatement";
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final String MOCKED_VERSION = "20191211";
  private static final String MOCKED_CONSENT_STATEMENT = "Content of consent statement";
  private static final JsonArray MOCKED_ARRAY_STRING_ROLES =
      Json.createArrayBuilder().add(ROLE_CONTROLLER).add(ROLE_PROCESSOR).build();
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(Long.MAX_VALUE);
  private final JsonArray mockedDatasetSchemaIds = createHashedIdsArray();
  private final JsonArray mockedBenefitIds = createHashedIdsArray();
  private final JsonArray mockedPurposeIds = createHashedIdsArray();
  private final JsonArray mockedThirdPartyIds = createHashedIdsArray();
  private final JsonArray mockedGroupCompanyIds = createHashedIdsArray();
  private final String mockedDataRetentionPolicyId = Hasher.hash(UUID.randomUUID().toString());
  private final JsonArray mockedOrganizationIds =
      Json.createArrayBuilder()
          .add(UUID.randomUUID().toString())
          .add(MOCKED_ORGANIZATION_ID)
          .build();
  private final JsonObject mockedOptionalThirdParties =
      Json.createObjectBuilder()
          .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, mockedThirdPartyIds)
          .add(THIRD_PARTY_DESCRIPTION, MOCKED_DESCRIPTION)
          .build();
  private final JsonObject mockedPurposes =
      Json.createObjectBuilder()
          .add(CONSENT_STATEMENT_TITLE, MOCKED_TITLE)
          .add(CONSENT_STATEMENT_PURPOSE_IDS, mockedPurposeIds)
          .add(CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS, mockedDatasetSchemaIds)
          .add(CONSENT_STATEMENT_BENEFIT_IDS, mockedBenefitIds)
          .add(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID, mockedDataRetentionPolicyId)
          .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, mockedThirdPartyIds)
          .add(CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES, mockedOptionalThirdParties)
          .build();
  private final JsonArray mockedOptionalPurposes =
      Json.createArrayBuilder().add(mockedPurposes).build();
  @Mock private Ledger ledger;
  private Key certificateKey = new Key(MOCKED_HOLDER_ID, 1);
  private RegisterConsentStatement registerConsentStatement;

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    registerConsentStatement = spy(new RegisterConsentStatement());
  }

  @Test
  public void invoke_ProperArgumentsAndPropertiesGiven_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile();
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(userProfile, argument);
    JsonObject putRecordsArgument = preparePutRecordArgument(argument, properties);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(registerConsentStatement.getCertificateKey()).thenReturn(certificateKey);
    doReturn(null)
        .when(registerConsentStatement)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(userProfile)
        .when(registerConsentStatement)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(registerConsentStatement)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(registerConsentStatement)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordsArgument);

    // Act
    registerConsentStatement.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(registerConsentStatement)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(registerConsentStatement)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordsArgument);
    verify(registerConsentStatement)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(registerConsentStatement)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(registerConsentStatement, times(4)).invokeSubContract(any(), any(), any());
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
              registerConsentStatement.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(registerConsentStatement, never()).invokeSubContract(any(), any(), any());
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
              registerConsentStatement.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(registerConsentStatement, never()).invokeSubContract(any(), any(), any());
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
              registerConsentStatement.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NAME_IS_MISSING);
    verify(registerConsentStatement, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WrongHolderIdGiven_ShouldThrowContractContextException() {
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

    JsonObject argument = prepareArgument();
    JsonObject validateArgumentArgument =
        prepareValidationArgument(argument, propertiesWithWrongHolderId);
    when(registerConsentStatement.getCertificateKey()).thenReturn(certificateKey);
    doReturn(null)
        .when(registerConsentStatement)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    Assertions.assertThatThrownBy(
            () -> {
              registerConsentStatement.invoke(
                  ledger, argument, Optional.of(propertiesWithWrongHolderId));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_NOT_MATCHED);
    verify(registerConsentStatement)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(registerConsentStatement).invokeSubContract(any(), any(), any());
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_COMPANY_ID).build();
  }

  private JsonObject prepareArgument() {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
        .add(GROUP_COMPANY_IDS, mockedGroupCompanyIds)
        .add(CONSENT_STATEMENT_VERSION, MOCKED_VERSION)
        .add(CONSENT_STATEMENT_STATUS, CONSENT_STATEMENT_DRAFT_STATUS)
        .add(CONSENT_STATEMENT_TITLE, MOCKED_TITLE)
        .add(CONSENT_STATEMENT_ABSTRACT, MOCKED_ABSTRACT)
        .add(CONSENT_STATEMENT_PURPOSE_IDS, mockedPurposeIds)
        .add(CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS, mockedDatasetSchemaIds)
        .add(CONSENT_STATEMENT_BENEFIT_IDS, mockedBenefitIds)
        .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, mockedThirdPartyIds)
        .add(CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES, mockedOptionalThirdParties)
        .add(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID, mockedDataRetentionPolicyId)
        .add(CONSENT_STATEMENT_OPTIONAL_PURPOSES, mockedOptionalPurposes)
        .add(CONSENT_STATEMENT, MOCKED_CONSENT_STATEMENT)
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
        .add(ASSET_VERSION, MOCKED_ASSET_VERSION)
        .build();
  }

  private JsonObject preparePutRecordArgument(JsonObject argument, JsonObject properties) {
    String assetId =
        String.format(
            "%s%s-%s-%s",
            properties.getString(ASSET_NAME),
            properties.getString(ASSET_VERSION),
            argument.getString(ORGANIZATION_ID),
            argument.getJsonNumber(CREATED_AT).toString());
    JsonObjectBuilder recordData =
        Json.createObjectBuilder()
            .add(COMPANY_ID, argument.getString(COMPANY_ID))
            .add(ORGANIZATION_ID, argument.getString(ORGANIZATION_ID))
            .add(CONSENT_STATEMENT_VERSION, argument.getString(CONSENT_STATEMENT_VERSION))
            .add(CONSENT_STATEMENT_TITLE, argument.getString(CONSENT_STATEMENT_TITLE))
            .add(CONSENT_STATEMENT_ABSTRACT, argument.getString(CONSENT_STATEMENT_ABSTRACT))
            .add(CONSENT_STATEMENT, argument.getString(CONSENT_STATEMENT))
            .add(GROUP_COMPANY_IDS, argument.getJsonArray(GROUP_COMPANY_IDS))
            .add(CONSENT_STATEMENT_STATUS, argument.getString(CONSENT_STATEMENT_STATUS))
            .add(
                CONSENT_STATEMENT_PURPOSE_IDS, argument.getJsonArray(CONSENT_STATEMENT_PURPOSE_IDS))
            .add(
                CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS,
                argument.getJsonArray(CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS))
            .add(
                CONSENT_STATEMENT_BENEFIT_IDS, argument.getJsonArray(CONSENT_STATEMENT_BENEFIT_IDS))
            .add(
                CONSENT_STATEMENT_THIRD_PARTY_IDS,
                argument.getJsonArray(CONSENT_STATEMENT_THIRD_PARTY_IDS))
            .add(
                CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES,
                argument.getJsonObject(CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES))
            .add(
                CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID,
                argument.getString(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID))
            .add(
                CONSENT_STATEMENT_OPTIONAL_PURPOSES,
                argument.getJsonArray(CONSENT_STATEMENT_OPTIONAL_PURPOSES))
            .add(CONSENT_STATEMENT, MOCKED_CONSENT_STATEMENT);
    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, recordData)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, argument.getJsonNumber(CREATED_AT))
        .build();
  }

  private JsonObject prepareUserProfile() {
    return Json.createObjectBuilder()
        .add(ORGANIZATION_IDS, mockedOrganizationIds)
        .add(ROLES, MOCKED_ARRAY_STRING_ROLES)
        .build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }

  private JsonObject preparePermissionValidationArgument(
      JsonObject userProfile, JsonObject arguments) {
    JsonArray ROLES = Json.createArrayBuilder().add(ROLE_CONTROLLER).build();
    return Json.createObjectBuilder()
        .add(ROLES_REQUIRED, ROLES)
        .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
        .add(ORGANIZATION_IDS_REQUIRED, userProfile.getJsonArray(ORGANIZATION_IDS))
        .add(
            ORGANIZATION_IDS_ARGUMENT,
            Json.createArrayBuilder().add(arguments.getString(ORGANIZATION_ID)).build())
        .build();
  }

  private JsonArray createHashedIdsArray() {

    return Json.createArrayBuilder()
        .add(Hasher.hash(UUID.randomUUID().toString()))
        .add(Hasher.hash(UUID.randomUUID().toString()))
        .build();
  }
}
