package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ABSTRACT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_BENEFIT_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_CHANGES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DESCRIPTION;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_OPTIONAL_PURPOSES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_PARENT_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_PURPOSE_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_STATUS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_THIRD_PARTY_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_TITLE;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_VERSION;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpdateConsentStatementVersionTest {
  private static final String SCHEMA_FILENAME = "update_consent_statement_version.json";
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final String MOCKED_VERSION = "20191212";
  private static final String MOCKED_CONSENT_STATEMENT = "Content of consent statement";
  private static final JsonArray MOCKED_ARRAY_STRING_ROLES =
      Json.createArrayBuilder().add(ROLE_CONTROLLER).add(ROLE_PROCESSOR).build();
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(200L);
  private static final String MOCKED_PARENT_CONSENT_STATEMENT_ID =
      CONSENT_STATEMENT + "/" + MOCKED_COMPANY_ID + "/" + MOCKED_CREATED_AT.toString();
  private static final String MOCKED_CONSENT_STATEMENT_CHANGES = "Version up";
  private final JsonArray mockedDatasetSchemaIds = createHashedIdsArray();
  private final JsonArray mockedPurposeIds = createHashedIdsArray();
  private final JsonArray mockedThirdPartyIds = createHashedIdsArray();
  private final JsonArray organizationIds =
      Json.createArrayBuilder()
          .add(MOCKED_ORGANIZATION_ID)
          .add(UUID.randomUUID().toString())
          .build();
  private final String mockedDataRetentionPolicyIds =
      "0e49ccac276736e59cc5ac3439891a2297a3ce06e8f78589aff1e17171dfbeda";
  @Mock private Ledger ledger;
  @Mock private Key certificateKey;
  private UpdateConsentStatementVersion updateConsentStatementVersion;

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    updateConsentStatementVersion = spy(new UpdateConsentStatementVersion());
  }

  @Test
  public void invoke_ProperArgumentsAndPropertiesGiven_ShouldUpdateAssetInLedger() {
    // arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile();
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(userProfile, argument);
    JsonObject getAssetRecordArgument = prepareGetAssetRecordArgument();
    JsonObject consentStatement = prepareConsentStatement();
    JsonObject putRecordArgument = preparePutAssetRecordArgument(properties, argument);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(updateConsentStatementVersion.getCertificateKey()).thenReturn(certificateKey);
    when(updateConsentStatementVersion.getCertificateKey().getHolderId())
        .thenReturn(MOCKED_HOLDER_ID);
    doReturn(userProfile)
        .when(updateConsentStatementVersion)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(consentStatement)
        .when(updateConsentStatementVersion)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    doReturn(null)
        .when(updateConsentStatementVersion)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(updateConsentStatementVersion)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    doReturn(null)
        .when(updateConsentStatementVersion)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // act
    updateConsentStatementVersion.invoke(ledger, argument, Optional.of(properties));

    // assert
    verify(updateConsentStatementVersion)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(updateConsentStatementVersion)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(updateConsentStatementVersion)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(updateConsentStatementVersion)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(updateConsentStatementVersion)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(updateConsentStatementVersion, times(5)).invokeSubContract(any(), any(), any());
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
              updateConsentStatementVersion.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(updateConsentStatementVersion, never()).invokeSubContract(any(), any(), any());
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
              updateConsentStatementVersion.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(updateConsentStatementVersion, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithWrongHolderId_ShouldThrowContractContextException() {
    // arrange
    JsonObject propertiesWithWrongHolderId =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                    .build())
            .add(HOLDER_ID, UUID.randomUUID().toString())
            .add(ASSET_NAME, "foo_name")
            .add(ASSET_VERSION, "foo_version")
            .build();
    JsonObject argument = prepareArgument();
    JsonObject validateArgumentArgument =
        prepareValidationArgument(argument, propertiesWithWrongHolderId);
    when(updateConsentStatementVersion.getCertificateKey()).thenReturn(certificateKey);
    when(updateConsentStatementVersion.getCertificateKey().getHolderId())
        .thenReturn(MOCKED_HOLDER_ID);
    doReturn(null)
        .when(updateConsentStatementVersion)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // act
    // assert
    assertThatThrownBy(
            () -> {
              updateConsentStatementVersion.invoke(
                  ledger, argument, Optional.of(propertiesWithWrongHolderId));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_NOT_MATCHED);
    verify(updateConsentStatementVersion)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(updateConsentStatementVersion).invokeSubContract(any(), any(), any());
  }

  private JsonObject prepareGetAssetRecordArgument() {
    return Json.createObjectBuilder()
        .add(ASSET_ID, MOCKED_PARENT_CONSENT_STATEMENT_ID)
        .add(RECORD_IS_HASHED, false)
        .build();
  }

  private JsonObject prepareConsentStatement() {
    return Json.createObjectBuilder()
        .add(CONSENT_STATEMENT_PARENT_ID, MOCKED_PARENT_CONSENT_STATEMENT_ID)
        .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
        .build();
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_COMPANY_ID).build();
  }

  private JsonObject prepareUserProfile() {
    return Json.createObjectBuilder()
        .add(ORGANIZATION_IDS, organizationIds)
        .add(ROLES, MOCKED_ARRAY_STRING_ROLES)
        .build();
  }

  private JsonObject prepareArgument() {
    JsonObjectBuilder argument =
        Json.createObjectBuilder()
            .add(CONSENT_STATEMENT_PARENT_ID, MOCKED_PARENT_CONSENT_STATEMENT_ID)
            .add(COMPANY_ID, MOCKED_COMPANY_ID)
            .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
            .add(GROUP_COMPANY_IDS, createHashedIdsArray())
            .add(CONSENT_STATEMENT_VERSION, MOCKED_VERSION)
            .add(CONSENT_STATEMENT_STATUS, "published")
            .add(CONSENT_STATEMENT_TITLE, "foo_title")
            .add(CONSENT_STATEMENT_ABSTRACT, "foo_abstract")
            .add(CONSENT_STATEMENT_CHANGES, MOCKED_CONSENT_STATEMENT_CHANGES)
            .add(CONSENT_STATEMENT_PURPOSE_IDS, mockedPurposeIds)
            .add(CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS, mockedDatasetSchemaIds)
            .add(CONSENT_STATEMENT_BENEFIT_IDS, createHashedIdsArray())
            .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, mockedThirdPartyIds);
    JsonObject optThirdParties =
        Json.createObjectBuilder()
            .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, createHashedIdsArray())
            .add(CONSENT_STATEMENT_DESCRIPTION, "foo_description")
            .build();
    argument
        .add(CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES, optThirdParties)
        .add(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID, mockedDataRetentionPolicyIds)
        .add(CONSENT_STATEMENT, MOCKED_CONSENT_STATEMENT);
    JsonArray optPurposes =
        Json.createArrayBuilder()
            .add(
                Json.createObjectBuilder()
                    .add(CONSENT_STATEMENT_TITLE, "foo_title")
                    .add(CONSENT_STATEMENT_DESCRIPTION, "foo_description")
                    .build())
            .build();
    argument.add(CONSENT_STATEMENT_OPTIONAL_PURPOSES, optPurposes);
    argument.add(CREATED_AT, MOCKED_CREATED_AT);
    return argument.build();
  }

  private JsonObject preparePutAssetRecordArgument(JsonObject properties, JsonObject argument) {
    JsonNumber createdAt = argument.getJsonNumber(CREATED_AT);
    String assetId =
        String.format(
            "%s%s-%s-%s",
            properties.getString(ASSET_NAME),
            properties.getString(ASSET_VERSION),
            argument.getString(ORGANIZATION_ID),
            createdAt.toString());
    JsonObject data =
        Json.createObjectBuilder()
            .add(CONSENT_STATEMENT_PARENT_ID, argument.getString(CONSENT_STATEMENT_PARENT_ID))
            .add(COMPANY_ID, argument.getString(COMPANY_ID))
            .add(ORGANIZATION_ID, argument.getString(ORGANIZATION_ID))
            .add(GROUP_COMPANY_IDS, argument.getJsonArray(GROUP_COMPANY_IDS))
            .add(CONSENT_STATEMENT_VERSION, argument.getString(CONSENT_STATEMENT_VERSION))
            .add(CONSENT_STATEMENT_STATUS, argument.getString(CONSENT_STATEMENT_STATUS))
            .add(CONSENT_STATEMENT_TITLE, argument.getString(CONSENT_STATEMENT_TITLE))
            .add(CONSENT_STATEMENT_ABSTRACT, argument.getString(CONSENT_STATEMENT_ABSTRACT))
            .add(CONSENT_STATEMENT_CHANGES, argument.getString(CONSENT_STATEMENT_CHANGES))
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
                argument.getJsonString(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID))
            .add(CONSENT_STATEMENT, argument.getString(CONSENT_STATEMENT))
            .add(
                CONSENT_STATEMENT_OPTIONAL_PURPOSES,
                argument.getJsonArray(CONSENT_STATEMENT_OPTIONAL_PURPOSES))
            .build();

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
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
        .add(ASSET_NAME, "foo_name")
        .add(ASSET_VERSION, "foo_version")
        .build();
  }

  private JsonArray createIDArray() {
    return Json.createArrayBuilder()
        .add("e19e2b28366aae6ef43df322262bbb3734155b4bacadbdf9841574df33af97e3")
        .add("0e49ccac276736e59cc5ac3439891a2297a3ce06e8f78589aff1e17171dfbeda")
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
