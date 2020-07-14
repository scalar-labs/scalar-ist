package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.CONSENTED_DETAIL;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATUS;
import static com.scalar.ist.common.Constants.CONSENT_STATUS_APPROVED;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_UPSERT;
import static com.scalar.ist.common.Constants.REJECTED_DETAIL;
import static com.scalar.ist.common.Constants.UPDATED_AT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.dl.ledger.crypto.CertificateEntry;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpsertConsentStatusTest {

  private static final String SCHEMA_FILENAME = "upsert_consent_status.json";
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final JsonNumber MOCKED_UPDATED_AT = Json.createValue(200L);
  private static final String MOCKED_CONSENT_STATUS = CONSENT_STATUS_APPROVED;
  private static final String MOCKED_CONSENT_STATEMENT_ID =
      CONSENT_STATEMENT + "/scalar-labs.com/" + MOCKED_UPDATED_AT.toString();
  private static final String MOCKED_ASSET_NAME = "consent";
  private static final String MOCKED_ASSET_VERSION = "01";
  @Mock private Ledger ledger;
  @Mock private CertificateEntry.Key certificateKey;
  private UpsertConsentStatus upsertConsentStatus;

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    upsertConsentStatus = spy(new UpsertConsentStatus());
  }

  @Test
  public void invoke_ProperArgumentAndPropertiesGiven_ShouldUpsertAssetInLedger() {
    // arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject getAssetRecordArgument = prepareGetAssetRecordArgument(argument);
    JsonObject putRecordArgument = preparePutRecordArgument(argument, properties);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(upsertConsentStatus.getCertificateKey()).thenReturn(certificateKey);
    when(upsertConsentStatus.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(null)
        .when(upsertConsentStatus)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(Json.createObjectBuilder().build())
        .when(upsertConsentStatus)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(upsertConsentStatus)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);

    // act
    upsertConsentStatus.invoke(ledger, argument, Optional.of(properties));

    // assert
    verify(upsertConsentStatus)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertConsentStatus).invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(upsertConsentStatus).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
  }

  @Test
  public void invoke_PropertiesMissingSchema_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = Json.createObjectBuilder().add(ASSET_NAME, MOCKED_ASSET_NAME).build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              upsertConsentStatus.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(upsertConsentStatus, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertiesMissingAssetName_ShouldThrowContractContextException() {
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
              upsertConsentStatus.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(upsertConsentStatus, never()).invokeSubContract(any(), any(), any());
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }

  private JsonObject prepareGetAssetRecordArgument(JsonObject argument) {
    return Json.createObjectBuilder()
        .add(ASSET_ID, argument.getString(CONSENT_STATEMENT_ID))
        .add(RECORD_IS_HASHED, false)
        .build();
  }

  private JsonObject prepareProperties() {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                .build())
        .add(ASSET_NAME, MOCKED_ASSET_NAME)
        .build();
  }

  private JsonObject prepareArgument() {
    return Json.createObjectBuilder()
        .add(CONSENT_STATEMENT_ID, MOCKED_CONSENT_STATEMENT_ID)
        .add(CONSENT_STATUS, MOCKED_CONSENT_STATUS)
        .add(CONSENTED_DETAIL, Json.createObjectBuilder().build())
        .add(REJECTED_DETAIL, Json.createObjectBuilder().build())
        .add(UPDATED_AT, MOCKED_UPDATED_AT)
        .build();
  }

  private JsonObject preparePutRecordArgument(JsonObject argument, JsonObject properties) {
    String consentStatementId = argument.getString(CONSENT_STATEMENT_ID);
    String dataSubjectId = MOCKED_HOLDER_ID;
    JsonNumber createdAt = argument.getJsonNumber(UPDATED_AT);
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId = String.join("-", assetName, consentStatementId, dataSubjectId);

    List<String> params =
        new ArrayList<>(
            Arrays.asList(CONSENT_STATEMENT_ID, CONSENT_STATUS, CONSENTED_DETAIL, REJECTED_DETAIL));
    JsonObjectBuilder data = createRecordData(params, argument);

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data.build())
        .add(RECORD_MODE, RECORD_MODE_UPSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
        .build();
  }

  private JsonObjectBuilder createRecordData(List<String> keys, JsonObject argument) {
    JsonObjectBuilder data = Json.createObjectBuilder();
    for (String key : keys)
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
    return data;
  }
}
