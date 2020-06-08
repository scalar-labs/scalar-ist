package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_IS_ALREADY_REGISTERED;
import static com.scalar.ist.common.Constants.ASSET_NOT_FOUND;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.DISALLOWED_CONTRACT_EXECUTION_ORDER;
import static com.scalar.ist.common.Constants.HASHED_ASSET_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.RECORD_MODE_UPDATE;
import static com.scalar.ist.common.Constants.RECORD_MODE_UPSERT;
import static com.scalar.ist.common.Constants.RECORD_SALT;
import static com.scalar.ist.common.Constants.REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING;
import static com.scalar.ist.common.Constants.SALT_IS_MISSING;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.crypto.CertificateEntry.Key;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.util.Hasher;
import com.scalar.ist.util.Util;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PutAssetRecordTest {
  private static final String SCHEMA_FILENAME = "put_asset_record.json";
  private static final String MOCKED_ASSET_ID =
      "consentstatement/" + UUID.randomUUID().toString() + "/" + System.currentTimeMillis();
  private static final String MOCKED_HOLDER_ID = "mockedHolderId";
  private static final String MOCKED_SALT = "64ju78r4er23";
  @Mock private Ledger ledger;
  @Mock private Key mockedKey;
  private PutAssetRecord putAssetRecord;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    putAssetRecord = spy(new PutAssetRecord());
    when(putAssetRecord.getCertificateKey()).thenReturn(mockedKey);
    when(mockedKey.getHolderId()).thenReturn(MOCKED_HOLDER_ID);
  }

  @Test
  public void invoke_InsertNonexistentAsset_ShouldInsertAsset() {
    // Arrange
    JsonObject argument = prepareArgument(RECORD_MODE_INSERT);
    JsonObject properties = prepareProperties();
    mockLedgerGetCall(argument, properties, null);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(putAssetRecord)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    JsonObject invokeResult = putAssetRecord.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(putAssetRecord).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(putAssetRecord).invokeSubContract(any(), any(), any());
    verify(ledger).get(getHashedAssetId(argument, properties));
    JsonObject data =
        Json.createObjectBuilder(argument.getJsonObject(RECORD_DATA))
            .add(ASSET_ID, argument.getString(ASSET_ID))
            .add(CREATED_AT, argument.getJsonNumber(CREATED_AT))
            .add(CREATED_BY, putAssetRecord.getCertificateKey().getHolderId())
            .build();
    verify(ledger).put(getHashedAssetId(argument, properties), data);
    assertEquals(
        invokeResult,
        Json.createObjectBuilder()
            .add(HASHED_ASSET_ID, getHashedAssetId(argument, properties))
            .build());
  }

  @Test
  public void invoke_WithoutProperty_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument(RECORD_MODE_INSERT);

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              putAssetRecord.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    verify(putAssetRecord, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutSchema_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument(RECORD_MODE_INSERT);
    JsonObject properties =
        Json.createObjectBuilder()
            .add(RECORD_SALT, MOCKED_SALT)
            .add(HOLDER_ID, MOCKED_HOLDER_ID)
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              putAssetRecord.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(putAssetRecord, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutSalt_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument(RECORD_MODE_INSERT);
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
              putAssetRecord.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(SALT_IS_MISSING);
    verify(putAssetRecord, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutHolderId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument(RECORD_MODE_INSERT);
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                    .build())
            .add(RECORD_SALT, MOCKED_SALT)
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              putAssetRecord.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(putAssetRecord, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_InsertAlreadyExistingAsset_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument(RECORD_MODE_INSERT);
    JsonObject properties = prepareProperties();
    mockLedgerGetCall(argument, properties, mock(Asset.class));
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(putAssetRecord)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    assertThatThrownBy(() -> putAssetRecord.invoke(ledger, argument, Optional.of(properties)))
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_IS_ALREADY_REGISTERED);
    verify(putAssetRecord).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(putAssetRecord).invokeSubContract(any(), any(), any());
    verify(ledger, never()).put(any(), any());
  }

  @Test
  public void invoke_UpdateAlreadyExistingAsset_ShouldUpdateAsset() {
    // Arrange
    JsonObject argument = prepareArgument(RECORD_MODE_UPDATE);
    JsonObject properties = prepareProperties();
    mockLedgerGetCall(argument, properties, mock(Asset.class));
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(putAssetRecord)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    JsonObject invokeResult = putAssetRecord.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(putAssetRecord).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(putAssetRecord).invokeSubContract(any(), any(), any());
    verify(ledger).get(getHashedAssetId(argument, properties));
    JsonObject data =
        Json.createObjectBuilder(argument.getJsonObject(RECORD_DATA))
            .add(ASSET_ID, argument.getString(ASSET_ID))
            .add(CREATED_AT, argument.getJsonNumber(CREATED_AT))
            .add(CREATED_BY, putAssetRecord.getCertificateKey().getHolderId())
            .build();
    verify(ledger).put(getHashedAssetId(argument, properties), data);
    assertEquals(
        invokeResult,
        Json.createObjectBuilder()
            .add(HASHED_ASSET_ID, getHashedAssetId(argument, properties))
            .build());
  }

  @Test
  public void invoke_UpdateNonexistentAsset_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument(RECORD_MODE_UPDATE);
    JsonObject properties = prepareProperties();
    mockLedgerGetCall(argument, properties, null);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(putAssetRecord)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    assertThatThrownBy(() -> putAssetRecord.invoke(ledger, argument, Optional.of(properties)))
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NOT_FOUND);
    verify(putAssetRecord).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(putAssetRecord).invokeSubContract(any(), any(), any());
    verify(ledger, never()).put(any(), any());
  }

  @Test
  public void invoke_UpsertExistingAsset_ShouldUpsertAsset() {
    // Arrange
    JsonObject argument = prepareArgument(RECORD_MODE_UPSERT);
    JsonObject properties = prepareProperties();
    mockLedgerGetCall(argument, properties, mock(Asset.class));
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(putAssetRecord)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    // Act
    JsonObject invokeResult = putAssetRecord.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(putAssetRecord).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(putAssetRecord).invokeSubContract(any(), any(), any());
    verify(ledger).get(getHashedAssetId(argument, properties));
    JsonObject data =
        Json.createObjectBuilder(argument.getJsonObject(RECORD_DATA))
            .add(ASSET_ID, argument.getString(ASSET_ID))
            .add(CREATED_AT, argument.getJsonNumber(CREATED_AT))
            .add(CREATED_BY, putAssetRecord.getCertificateKey().getHolderId())
            .build();
    verify(ledger).put(getHashedAssetId(argument, properties), data);
    assertEquals(
        invokeResult,
        Json.createObjectBuilder()
            .add(HASHED_ASSET_ID, getHashedAssetId(argument, properties))
            .build());
  }

  @Test
  public void invoke_UpsertNonExistentAsset_ShouldUpsertAsset() {
    // Arrange
    JsonObject argument = prepareArgument(RECORD_MODE_UPSERT);
    JsonObject properties = prepareProperties();
    mockLedgerGetCall(argument, properties, null);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(putAssetRecord)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    JsonObject invokeResult = putAssetRecord.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(putAssetRecord).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(putAssetRecord).invokeSubContract(any(), any(), any());
    verify(ledger).get(getHashedAssetId(argument, properties));
    JsonObject data =
        Json.createObjectBuilder(argument.getJsonObject(RECORD_DATA))
            .add(ASSET_ID, argument.getString(ASSET_ID))
            .add(CREATED_AT, argument.getJsonNumber(CREATED_AT))
            .add(CREATED_BY, putAssetRecord.getCertificateKey().getHolderId())
            .build();
    verify(ledger).put(getHashedAssetId(argument, properties), data);
    assertEquals(
        invokeResult,
        Json.createObjectBuilder()
            .add(HASHED_ASSET_ID, getHashedAssetId(argument, properties))
            .build());
  }

  @Test
  public void invoke_WithRootUser_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArgument(RECORD_MODE_INSERT);
    JsonObject properties = prepareProperties();
    String hashedId = Hasher.hash(properties.getString(RECORD_SALT), argument.getString(ASSET_ID));
    Asset asset = mock(Asset.class);
    when(ledger.get(hashedId)).thenReturn(Optional.of(asset));
    when(putAssetRecord.isRoot()).thenReturn(true);

    // act
    // assert
    assertThatThrownBy(
            () -> {
              putAssetRecord.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(DISALLOWED_CONTRACT_EXECUTION_ORDER);
  }

  @AfterEach
  public void verifyLedgerScanNeverCalled() {
    verify(ledger, never()).scan(any());
  }

  private JsonObject prepareProperties() {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                .build())
        .add(RECORD_SALT, MOCKED_SALT)
        .add(HOLDER_ID, MOCKED_HOLDER_ID)
        .build();
  }

  private JsonObject prepareArgument(String mode) {
    JsonObject recordData = Json.createObjectBuilder().add("foo", "bar").build();

    return Json.createObjectBuilder()
        .add(RECORD_DATA, recordData)
        .add(ASSET_ID, MOCKED_ASSET_ID)
        .add(RECORD_MODE, mode)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, System.currentTimeMillis())
        .build();
  }

  private void mockLedgerGetCall(JsonObject argument, JsonObject properties, Asset result) {
    when(ledger.get(getHashedAssetId(argument, properties)))
        .thenReturn(Optional.ofNullable(result));
  }

  private String getHashedAssetId(JsonObject argument, JsonObject properties) {
    if (argument.getBoolean(RECORD_IS_HASHED)) {
      return argument.getString(ASSET_ID);
    } else {
      return Hasher.hash(properties.getString(RECORD_SALT), argument.getString(ASSET_ID));
    }
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }
}
