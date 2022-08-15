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
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DRAFT_STATUS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ID;
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
import static com.scalar.ist.common.Constants.RECORD_MODE_UPDATE;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
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

import com.scalar.dl.ledger.crypto.CertificateEntry;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import com.scalar.ist.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

public class UpdateConsentStatementRevisionTest {
  private static final String SCHEMA_FILENAME = "update_consent_statement_revision.json";
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_ABSTRACT = "Abstract of ConsentStatement";
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final String MOCKED_VERSION = "Version-1a";
  private static final String MOCKED_TITLE = "Title of consent statment";
  private static final String MOCKED_CONSENT_STATEMENT = "Content of consent statement";
  private static final String MOCKED_DATA_RETENTION_POLICY_ID = UUID.randomUUID().toString();
  private static final JsonArray MOCKED_ARRAY_STRING_ROLES =
      Json.createArrayBuilder().add(ROLE_CONTROLLER).add(ROLE_PROCESSOR).build();
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(200L);
  private static final String MOCKED_CONSENT_STATEMENT_ID =
      CONSENT_STATEMENT + "/" + MOCKED_COMPANY_ID + "/" + MOCKED_CREATED_AT.toString();
  private static final String MOCKED_CONSENT_STATEMENT_CHANGES = "changes";
  private final JsonArray organizationIds =
      Json.createArrayBuilder()
          .add(MOCKED_ORGANIZATION_ID)
          .add(UUID.randomUUID().toString())
          .build();
  private final JsonArray datasetSchemaIds = createIDArray();
  private final JsonArray purposeIds = createIDArray();
  private final JsonArray thirdPartyIds = createIDArray();

  @Mock private Ledger ledger;
  private CertificateEntry.Key certificateKey = new CertificateEntry.Key(MOCKED_HOLDER_ID, 1);
  private UpdateConsentStatementRevision updateConsentStatementRevision;

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    updateConsentStatementRevision = spy(new UpdateConsentStatementRevision());
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
    JsonObject putRecordArgument = preparePutAssetRecordArgument(argument);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(updateConsentStatementRevision.getCertificateKey()).thenReturn(certificateKey);
    doReturn(null)
        .when(updateConsentStatementRevision)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(userProfile)
        .when(updateConsentStatementRevision)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(updateConsentStatementRevision)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(consentStatement)
        .when(updateConsentStatementRevision)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(updateConsentStatementRevision)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);

    // act
    updateConsentStatementRevision.invoke(ledger, argument, Optional.of(properties));

    // assert
    verify(updateConsentStatementRevision)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(updateConsentStatementRevision)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(updateConsentStatementRevision)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(updateConsentStatementRevision)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(updateConsentStatementRevision)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(updateConsentStatementRevision, times(5)).invokeSubContract(any(), any(), any());
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
            .add(ASSET_NAME, CONSENT_STATEMENT)
            .add(ASSET_VERSION, MOCKED_VERSION)
            .build();
    JsonObject argument = prepareArgument();
    JsonObject validateArgumentArgument =
        prepareValidationArgument(argument, propertiesWithWrongHolderId);
    when(updateConsentStatementRevision.getCertificateKey()).thenReturn(certificateKey);
    doReturn(null)
        .when(updateConsentStatementRevision)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // act
    // assert
    assertThatThrownBy(
            () -> {
              updateConsentStatementRevision.invoke(
                  ledger, argument, Optional.of(propertiesWithWrongHolderId));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_NOT_MATCHED);
    verify(updateConsentStatementRevision)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(updateConsentStatementRevision).invokeSubContract(any(), any(), any());
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
              updateConsentStatementRevision.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(updateConsentStatementRevision, never()).invokeSubContract(any(), any(), any());
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
              updateConsentStatementRevision.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(updateConsentStatementRevision, never()).invokeSubContract(any(), any(), any());
  }

  private JsonObject prepareGetAssetRecordArgument() {
    return Json.createObjectBuilder()
        .add(ASSET_ID, MOCKED_CONSENT_STATEMENT_ID)
        .add(RECORD_IS_HASHED, false)
        .build();
  }

  private JsonObject prepareConsentStatement() {
    return Json.createObjectBuilder()
        .add(CONSENT_STATEMENT_ID, MOCKED_CONSENT_STATEMENT_ID)
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
    return Json.createObjectBuilder()
        .add(CONSENT_STATEMENT_ID, MOCKED_CONSENT_STATEMENT_ID)
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
        .add(CONSENT_STATEMENT_ABSTRACT, MOCKED_ABSTRACT)
        .add(CONSENT_STATEMENT_VERSION, MOCKED_VERSION)
        .add(CONSENT_STATEMENT_TITLE, MOCKED_TITLE)
        .add(CONSENT_STATEMENT_CHANGES, MOCKED_CONSENT_STATEMENT_CHANGES)
        .add(CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS, datasetSchemaIds)
        .add(CONSENT_STATEMENT_PURPOSE_IDS, purposeIds)
        .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, thirdPartyIds)
        .add(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID, MOCKED_DATA_RETENTION_POLICY_ID)
        .add(CONSENT_STATEMENT, MOCKED_CONSENT_STATEMENT)
        .add(UPDATED_AT, MOCKED_CREATED_AT)
        .build();
  }

  private JsonObject preparePutAssetRecordArgument(JsonObject argument) {
    String consentStatementId = argument.getString(CONSENT_STATEMENT_ID);
    JsonNumber createdAt = argument.getJsonNumber(UPDATED_AT);
    List<String> params =
        new ArrayList<>(
            Arrays.asList(
                COMPANY_ID,
                ORGANIZATION_ID,
                GROUP_COMPANY_IDS,
                CONSENT_STATEMENT_VERSION,
                CONSENT_STATEMENT_CHANGES,
                CONSENT_STATEMENT_ABSTRACT,
                CONSENT_STATEMENT_TITLE,
                CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS,
                CONSENT_STATEMENT_PURPOSE_IDS,
                CONSENT_STATEMENT_BENEFIT_IDS,
                CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID,
                CONSENT_STATEMENT_THIRD_PARTY_IDS,
                CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES,
                CONSENT_STATEMENT,
                CONSENT_STATEMENT_OPTIONAL_PURPOSES));

    JsonObjectBuilder data = createRecordData(params, argument);
    data.add(
        CONSENT_STATEMENT_STATUS,
        argument.getString(CONSENT_STATEMENT_STATUS, CONSENT_STATEMENT_DRAFT_STATUS));

    return Json.createObjectBuilder()
        .add(ASSET_ID, consentStatementId)
        .add(RECORD_DATA, data.build())
        .add(RECORD_MODE, RECORD_MODE_UPDATE)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
        .build();
  }

  private JsonObjectBuilder createRecordData(List<String> keys, JsonObject argument) {
    JsonObjectBuilder data = Json.createObjectBuilder();
    for (String key : keys) {
      if (argument.containsKey(key)) {
        switch (argument.get(key).getValueType()) {
          case ARRAY:
            data.add(key, argument.getJsonArray(key));
            break;
          case OBJECT:
            data.add(key, argument.getJsonObject(key));
            break;
          case STRING:
            data.add(key, argument.getString(key));
            break;
          case NUMBER:
            data.add(key, argument.getJsonNumber(key));
            break;
          default:
            throw new ContractContextException(
                "The type " + argument.get(key).getValueType() + " is not supported");
        }
      }
    }
    return data;
  }

  private JsonObject prepareProperties() {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                .build())
        .add(HOLDER_ID, MOCKED_HOLDER_ID)
        .add(ASSET_NAME, CONSENT_STATEMENT)
        .add(ASSET_VERSION, MOCKED_VERSION)
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

  private JsonArray createIDArray() {
    return Json.createArrayBuilder().add("id1").add("id2").build();
  }
}
