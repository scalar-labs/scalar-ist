package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NOT_FOUND;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.DISALLOWED_CONTRACT_EXECUTION_ORDER;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_SALT;
import static com.scalar.ist.common.Constants.REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING;
import static com.scalar.ist.common.Constants.SALT_IS_MISSING;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.util.Hasher;
import com.scalar.ist.util.Util;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GetAssetRecordTest {
  private static final String SCHEMA_FILENAME = "get_asset_record.json";
  private static final String MOCKED_ASSET_ID =
      "consentstatement/" + UUID.randomUUID().toString() + "/" + System.currentTimeMillis();
  private static final String MOCKED_SALT = "mocked_salt";
  private static final String MOCKED_SCHEMA = "mocked_schema";
  @Mock private Ledger ledger;
  private GetAssetRecord getAssetRecord;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    getAssetRecord = spy(new GetAssetRecord());
  }

  private JsonObject prepareArguments() {
    return Json.createObjectBuilder()
        .add(ASSET_ID, MOCKED_ASSET_ID)
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
        .add(RECORD_SALT, MOCKED_SALT)
        .build();
  }

  @Test
  public void invoke_ProperArgumentGivenAndAssetExists_ShouldRetrieveAssetDataFromLedger() {
    // arrange
    JsonObject argument = prepareArguments();
    JsonObject properties = prepareProperties();
    String hashedId = Hasher.hash(properties.getString(RECORD_SALT), argument.getString(ASSET_ID));
    Asset asset = mock(Asset.class);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(getAssetRecord)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    when(ledger.get(hashedId)).thenReturn(Optional.of(asset));
    when(asset.data())
        .thenReturn(Json.createObjectBuilder().add(ASSET_ID, MOCKED_ASSET_ID).build());
    when(getAssetRecord.isRoot()).thenReturn(false);

    // act
    JsonObject assetData = getAssetRecord.invoke(ledger, argument, Optional.of(properties));
    JsonObject mockedAssetData = Json.createObjectBuilder().add(ASSET_ID, MOCKED_ASSET_ID).build();

    // assert
    assertThat(assetData).isEqualTo(mockedAssetData);
    verify(getAssetRecord).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(getAssetRecord, times(1)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithRootUser_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArguments();
    JsonObject properties = prepareProperties();
    when(getAssetRecord.isRoot()).thenReturn(true);

    // act
    // assert
    assertThatThrownBy(
            () -> {
              getAssetRecord.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(DISALLOWED_CONTRACT_EXECUTION_ORDER);
  }

  @Test
  public void invoke_ProperArgumentGivenAndAssetNotExisting_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArguments();
    JsonObject properties = prepareProperties();
    String hashedId = Hasher.hash(properties.getString(RECORD_SALT), argument.getString(ASSET_ID));
    when(ledger.get(hashedId)).thenReturn(Optional.empty());
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(getAssetRecord)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    // act
    // assert
    assertThatThrownBy(
            () -> {
              getAssetRecord.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NOT_FOUND);
    verify(getAssetRecord).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(getAssetRecord, times(1)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_SaltPropertyMissing_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArguments();
    JsonObject properties =
        Json.createObjectBuilder().add(CONTRACT_ARGUMENT_SCHEMA, MOCKED_SCHEMA).build();

    // act
    // assert
    assertThatThrownBy(
            () -> {
              getAssetRecord.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(SALT_IS_MISSING);
    verify(ledger, never()).get(anyString());
  }

  @Test
  public void invoke_ContractArgumentSchemaMissing_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArguments();
    JsonObject properties = Json.createObjectBuilder().add(RECORD_SALT, MOCKED_SALT).build();

    // act
    // assert
    assertThatThrownBy(
            () -> {
              getAssetRecord.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(ledger, never()).get(anyString());
  }

  @Test
  public void invoke_AllPropertiesMissing_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArguments();

    // act
    // assert
    assertThatThrownBy(
            () -> {
              getAssetRecord.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    verify(ledger, never()).get(anyString());
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }
}
