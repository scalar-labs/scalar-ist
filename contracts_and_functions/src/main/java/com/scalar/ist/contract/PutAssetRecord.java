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
import static com.scalar.ist.common.Constants.RECORD_SALT;
import static com.scalar.ist.common.Constants.REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING;
import static com.scalar.ist.common.Constants.SALT_IS_MISSING;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.hash.HashCode;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import org.hashids.Hashids;

public class PutAssetRecord extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validate(ledger, arguments, properties);
    String hashedAssetId = getHashedAssetId(arguments, properties.get());
    JsonObject record =
        Json.createObjectBuilder(arguments.getJsonObject(RECORD_DATA))
            .add(ASSET_ID, arguments.getString(ASSET_ID))
            .add(CREATED_AT, arguments.getJsonNumber(CREATED_AT))
            .add(CREATED_BY, getCertificateKey().getHolderId())
            .build();
    ledger.put(hashedAssetId, record);

    return Json.createObjectBuilder().add(HASHED_ASSET_ID, hashedAssetId).build();
  }

  private void validate(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validateExecutionOrder();
    validateProperties(properties);
    validateArguments(ledger, arguments, properties.get());
    validateRecordMode(ledger, arguments, properties.get());
  }

  private void validateExecutionOrder() {
    if (isRoot()) {
      throw new ContractContextException(DISALLOWED_CONTRACT_EXECUTION_ORDER);
    }
  }

  private void validateProperties(Optional<JsonObject> optionalProperties) {
    if (!optionalProperties.isPresent()) {
      throw new ContractContextException(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    }
    JsonObject properties = optionalProperties.get();
    if (!properties.containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
    if (!properties.containsKey(RECORD_SALT)) {
      throw new ContractContextException(SALT_IS_MISSING);
    }
    if (!properties.containsKey(HOLDER_ID)) {
      throw new ContractContextException(HOLDER_ID_IS_MISSING);
    }
  }

  private void validateArguments(Ledger ledger, JsonObject arguments, JsonObject properties) {
    JsonObject schema = properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA);
    JsonObject validateArgumentJson =
        Json.createObjectBuilder()
            .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, arguments)
            .add(VALIDATE_ARGUMENT_SCHEMA, schema)
            .build();

    invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentJson);
  }

  public void validateRecordMode(Ledger ledger, JsonObject arguments, JsonObject properties) {
    boolean isAssetPresent = ledger.get(getHashedAssetId(arguments, properties)).isPresent();
    String recordMode = arguments.getString(RECORD_MODE);
    if (recordMode.equals(RECORD_MODE_INSERT) && isAssetPresent) {
      throw new ContractContextException(ASSET_IS_ALREADY_REGISTERED);
    }
    if (recordMode.equals(RECORD_MODE_UPDATE) && !isAssetPresent) {
      throw new ContractContextException(ASSET_NOT_FOUND);
    }
  }

  private String getHashedAssetId(JsonObject arguments, JsonObject properties) {
    if (arguments.getBoolean(RECORD_IS_HASHED)) {
      return arguments.getString(ASSET_ID);
    } else {
      String hexaAssetId =
          HashCode.fromBytes(arguments.getString(ASSET_ID).getBytes(StandardCharsets.UTF_8))
              .toString();
      return new Hashids(properties.getString(RECORD_SALT)).encodeHex(hexaAssetId);
    }
  }

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
