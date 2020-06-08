package com.scalar.ist.contract;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.scalar.ist.common.Constants.*;

public class UpsertConsentStatus extends Contract {

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
    JsonObjectBuilder data =
        createRecordData(properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA), params, argument);
    data.add(CREATED_BY, dataSubjectId);

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data.build())
        .add(RECORD_MODE, RECORD_MODE_UPSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
        .build();
  }

  private JsonObjectBuilder createRecordData(
      JsonObject assetSchema, List<String> keys, JsonObject argument) {
    JsonObjectBuilder data = Json.createObjectBuilder();
    JsonObject metadata = assetSchema.getJsonObject(PROPERTIES).asJsonObject();
    for (String key : keys)
      if (argument.containsKey(key) && metadata.containsKey(key)) {
        switch (metadata.getJsonObject(key).asJsonObject().getString(ASSET_TYPE)) {
          case ASSET_ARRAY_TYPE:
            data.add(key, argument.getJsonArray(key));
            break;
          case ASSET_OBJECT_TYPE:
            data.add(key, argument.getJsonObject(key));
            break;
          case ASSET_STRING_TYPE:
            data.add(key, argument.getString(key));
            break;
          case ASSET_INTEGER_TYPE:
            data.add(key, argument.getJsonNumber(key));
            break;
          default:
            throw new ContractContextException(
                "The type " + argument.get(key).getValueType() + " is not supported");
        }
      }
    return data;
  }

  private void validate(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArgument(ledger, argument, properties.get());
    validateConsentStatement(ledger, argument);
  }

  private void validateProperties(Optional<JsonObject> properties) {
    if (!properties.isPresent() || !properties.get().containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
    if (!properties.get().containsKey(ASSET_NAME)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
  }

  private void validateArgument(Ledger ledger, JsonObject argument, JsonObject properties) {
    JsonObject schema = properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA);

    JsonObject validateArgumentJson =
        Json.createObjectBuilder()
            .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
            .add(VALIDATE_ARGUMENT_SCHEMA, schema)
            .build();

    invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentJson);
  }

  private void validateConsentStatement(Ledger ledger, JsonObject arguments) {
    JsonObject getAssetRecordArgument =
        Json.createObjectBuilder()
            .add(ASSET_ID, arguments.getString(CONSENT_STATEMENT_ID))
            .add(RECORD_IS_HASHED, false)
            .build();

    invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
  }

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
