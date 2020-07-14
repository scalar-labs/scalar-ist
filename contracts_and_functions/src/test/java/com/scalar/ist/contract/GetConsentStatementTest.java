package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ABSTRACT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_CHANGES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_PURPOSE_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_THIRD_PARTY_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_VERSION;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.HASHED_CONSENT_STATEMENT_ID;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.util.Hasher;
import com.scalar.ist.util.Util;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GetConsentStatementTest {
  private static final String SCHEMA_FILENAME = "get_consent_statement.json";
  private static final String MOCKED_CONSENT_STATEMENT = "mocked_consent_statement";
  private static final String MOCKED_CONSENT_STATEMENT_VERSION = "mocked_version";
  private static final String MOCKED_CONSENT_STATEMENT_CHANGES = "mocked_changes";
  private static final String MOCKED_CONSENT_STATEMENT_ABSTRACT = "mocked_abstract";
  private static final JsonArray MOCKED_CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS =
      createHashedIdsArray();
  private static final JsonArray MOCKED_CONSENT_STATEMENT_PURPOSE_IDS = createHashedIdsArray();
  private static final JsonArray MOCKED_CONSENT_STATEMENT_THIRD_PARTY_IDS = createHashedIdsArray();
  private static final String MOCKED_CONSENT_STATEMENT_DATA_RETENTION_ID =
      "mocked_data_retention_id";
  private static final String MOCKED_HASHED_CONSENT_STATEMENT_ID = UUID.randomUUID().toString();
  @Mock private Ledger ledger;
  private GetConsentStatement getConsentStatement;

  private static JsonArray createHashedIdsArray() {
    return Json.createArrayBuilder()
        .add(Hasher.hash(UUID.randomUUID().toString()))
        .add(Hasher.hash(UUID.randomUUID().toString()))
        .build();
  }

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    getConsentStatement = spy(new GetConsentStatement());
  }

  @Test
  public void invoke_ProperArgumentGivenAndAssetExists_ShouldRetrieveConsentStatement() {
    // arrange
    JsonObject arguments = prepareArguments();
    JsonObject properties = prepareProperties();
    JsonObject getAssetRecordArgument = prepareAssetRecordArgument(arguments);
    JsonObject consentStatement = prepareAssetRecord();
    JsonObject validateArgumentArgument = prepareValidationArgument(arguments, properties);
    doReturn(null)
        .when(getConsentStatement)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(consentStatement)
        .when(getConsentStatement)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);

    // act
    JsonObject consentStatementObject =
        getConsentStatement.invoke(ledger, arguments, Optional.of(properties));

    JsonObject mockedConsentStatement =
        Json.createObjectBuilder()
            .add(CONSENT_STATEMENT, MOCKED_CONSENT_STATEMENT)
            .add(CONSENT_STATEMENT_VERSION, MOCKED_CONSENT_STATEMENT_VERSION)
            .add(CONSENT_STATEMENT_CHANGES, MOCKED_CONSENT_STATEMENT_CHANGES)
            .add(CONSENT_STATEMENT_ABSTRACT, MOCKED_CONSENT_STATEMENT_ABSTRACT)
            .add(
                CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS, MOCKED_CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS)
            .add(CONSENT_STATEMENT_PURPOSE_IDS, MOCKED_CONSENT_STATEMENT_PURPOSE_IDS)
            .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, MOCKED_CONSENT_STATEMENT_THIRD_PARTY_IDS)
            .add(
                CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID,
                MOCKED_CONSENT_STATEMENT_DATA_RETENTION_ID)
            .build();

    // assert
    assertThat(consentStatementObject).isEqualTo(mockedConsentStatement);
    verify(getConsentStatement)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(getConsentStatement).invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(getConsentStatement, times(2)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyMissing_ShouldThrowContractContextException() {
    // arrange
    JsonObject arguments = prepareArguments();

    // act
    // assert
    assertThatThrownBy(
            () -> {
              getConsentStatement.invoke(ledger, arguments, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    verify(ledger, never()).get(anyString());
  }

  private JsonObject prepareArguments() {
    return Json.createObjectBuilder()
        .add(HASHED_CONSENT_STATEMENT_ID, MOCKED_HASHED_CONSENT_STATEMENT_ID)
        .build();
  }

  private JsonObject prepareProperties() {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                .build())
        .build();
  }

  private JsonObject prepareAssetRecordArgument(JsonObject arguments) {
    return Json.createObjectBuilder()
        .add(ASSET_ID, arguments.getString(HASHED_CONSENT_STATEMENT_ID))
        .add(RECORD_IS_HASHED, true)
        .build();
  }

  private JsonObject prepareAssetRecord() {
    return Json.createObjectBuilder()
        .add(CONSENT_STATEMENT, MOCKED_CONSENT_STATEMENT)
        .add(CONSENT_STATEMENT_VERSION, MOCKED_CONSENT_STATEMENT_VERSION)
        .add(CONSENT_STATEMENT_CHANGES, MOCKED_CONSENT_STATEMENT_CHANGES)
        .add(CONSENT_STATEMENT_ABSTRACT, MOCKED_CONSENT_STATEMENT_ABSTRACT)
        .add(CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS, MOCKED_CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS)
        .add(CONSENT_STATEMENT_PURPOSE_IDS, MOCKED_CONSENT_STATEMENT_PURPOSE_IDS)
        .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, MOCKED_CONSENT_STATEMENT_THIRD_PARTY_IDS)
        .add(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID, MOCKED_CONSENT_STATEMENT_DATA_RETENTION_ID)
        .build();
  }

  private JsonObject prepareValidationArgument(JsonObject arguments, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, arguments)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }
}
