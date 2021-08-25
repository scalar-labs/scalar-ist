package com.scalar.ist.contract;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.hash.HashCode;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.AssetFilter;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import org.hashids.Hashids;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NOT_FOUND;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.DISALLOWED_CONTRACT_EXECUTION_ORDER;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_END_VERSION;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_LIMIT;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_SCAN;
import static com.scalar.ist.common.Constants.RECORD_SALT;
import static com.scalar.ist.common.Constants.RECORD_START_VERSION;
import static com.scalar.ist.common.Constants.RECORD_VERSION;
import static com.scalar.ist.common.Constants.RECORD_VERSIONS;
import static com.scalar.ist.common.Constants.RECORD_VERSION_ORDER;
import static com.scalar.ist.common.Constants.REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING;
import static com.scalar.ist.common.Constants.SALT_IS_MISSING;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;

public class GetAssetRecord extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);

    String assetId = getHashedAssetId(argument, properties.get());

    if (argument.containsKey(RECORD_MODE)
        && argument.getString(RECORD_MODE).equals(RECORD_MODE_SCAN)) {
      AssetFilter assetFilter = new AssetFilter(assetId);
      if (argument.containsKey(RECORD_START_VERSION)) {
        assetFilter.withStartAge(argument.getInt(RECORD_START_VERSION), true);
      }
      if (argument.containsKey(RECORD_END_VERSION)) {
        assetFilter.withEndAge(argument.getInt(RECORD_END_VERSION), true);
      }
      if (argument.containsKey(RECORD_LIMIT)) {
        assetFilter.withLimit(argument.getInt(RECORD_LIMIT));
      }
      if (argument.containsKey(RECORD_VERSION_ORDER)) {
        assetFilter.withAgeOrder(
            AssetFilter.AgeOrder.valueOf(argument.getString(RECORD_VERSION_ORDER)));
      }
      JsonArrayBuilder recordVersions = Json.createArrayBuilder();
      ledger
          .scan(assetFilter)
          .forEach(
              asset -> {
                recordVersions.add(
                    Json.createObjectBuilder()
                        .add(RECORD_VERSION, asset.age())
                        .add(RECORD_DATA, asset.data())
                        .build());
              });

      return Json.createObjectBuilder().add(RECORD_VERSIONS, recordVersions.build()).build();
    }

    return ledger
        .get(assetId)
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
