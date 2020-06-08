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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.hash.HashCode;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import org.hashids.Hashids;

public class GetAssetRecord extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);

    return ledger
        .get(getHashedAssetId(argument, properties.get()))
        .map(Asset::data)
        .orElseThrow(() -> new ContractContextException(ASSET_NOT_FOUND));
  }

  private void validate(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateExecutionOrder();
    validateProperties(properties);
    validateArguments(ledger, argument, properties.get());
  }

  private void validateProperties(Optional<JsonObject> properties) {
    if (!properties.isPresent()) {
      throw new ContractContextException(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    }
    if (!properties.get().containsKey(RECORD_SALT)) {
      throw new ContractContextException(SALT_IS_MISSING);
    }
    if (!properties.get().containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
  }

  private void validateExecutionOrder() {
    if (isRoot()) {
      throw new ContractContextException(DISALLOWED_CONTRACT_EXECUTION_ORDER);
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
