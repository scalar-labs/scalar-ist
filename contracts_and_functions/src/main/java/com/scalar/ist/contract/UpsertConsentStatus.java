package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.CONSENTED_DETAIL;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATUS;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
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

import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class UpsertConsentStatus extends BaseContract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);
    JsonObject putAssetRecordArgument = createPutAssetRecordArgument(argument, properties.get());
    return invokeSubContract(PUT_ASSET_RECORD, ledger, putAssetRecordArgument);
  }

  private JsonObject createPutAssetRecordArgument(JsonObject argument, JsonObject properties) {
    String consentStatementId = argument.getString(CONSENT_STATEMENT_ID);
    String dataSubjectId = getCertificateKey().getHolderId();
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
        JsonValue.ValueType argumentValueType = argument.get(key).getValueType();
        if (argumentValueType == JsonValue.ValueType.ARRAY) {
          data.add(key, argument.getJsonArray(key));
        } else if (argumentValueType == JsonValue.ValueType.OBJECT) {
          data.add(key, argument.getJsonObject(key));
        } else if (argumentValueType == JsonValue.ValueType.STRING) {
          data.add(key, argument.getString(key));
        } else if (argumentValueType == JsonValue.ValueType.NUMBER) {
          data.add(key, argument.getJsonNumber(key));
        } else {
          throw new ContractContextException("The type " + argumentValueType + " is not supported");
        }
      }
    return data;
  }

  private void validate(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArguments(ledger, argument, properties.get());
    validateConsentStatement(ledger, argument);
  }

  @Override
  protected void validateProperties(Optional<JsonObject> properties) {
    if (!properties.isPresent() || !properties.get().containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
    if (!properties.get().containsKey(ASSET_NAME)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
  }

  private void validateConsentStatement(Ledger ledger, JsonObject arguments) {
    JsonObject getAssetRecordArgument =
        Json.createObjectBuilder()
            .add(ASSET_ID, arguments.getString(CONSENT_STATEMENT_ID))
            .add(RECORD_IS_HASHED, false)
            .build();

    invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
  }
}
