package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.DISALLOWED_CONTRACT_EXECUTION_ORDER;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.REQUIRED_ATTRIBUTES_FOR_USER_PROFILE_ARE_MISSING;
import static com.scalar.ist.common.Constants.REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;

public class GetUserProfile extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);

    String assetName =
        properties.get().getString(ASSET_NAME) + properties.get().getString(ASSET_VERSION, "");
    String assetId =
        String.format(
            "%s-%s-%s",
            assetName, argument.getString(COMPANY_ID), getCertificateKey().getHolderId());
    JsonObject getAssetArgument =
        Json.createObjectBuilder().add(ASSET_ID, assetId).add(RECORD_IS_HASHED, false).build();
    JsonObject userProfile = invokeSubContract(GET_ASSET_RECORD, ledger, getAssetArgument);

    validateUserProfile(userProfile);

    return Json.createObjectBuilder()
        .add(ORGANIZATION_IDS, userProfile.getJsonArray(ORGANIZATION_IDS))
        .add(ROLES, userProfile.getJsonArray(ROLES))
        .build();
  }

  private void validate(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateExecutionOrder();
    validateProperties(properties);
    validateArguments(ledger, argument, properties.get());
  }

  private void validateExecutionOrder() {
    if (isRoot()) {
      throw new ContractContextException(DISALLOWED_CONTRACT_EXECUTION_ORDER);
    }
  }

  private void validateUserProfile(JsonObject userProfile) {
    if (!userProfile.containsKey(ROLES) || !userProfile.containsKey(ORGANIZATION_IDS)) {
      throw new ContractContextException(REQUIRED_ATTRIBUTES_FOR_USER_PROFILE_ARE_MISSING);
    }
  }

  private void validateProperties(Optional<JsonObject> properties) {
    if (!properties.isPresent()) {
      throw new ContractContextException(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    }
    if (!properties.get().containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
    if (!properties.get().containsKey(ASSET_NAME)) {
      throw new ContractContextException(ASSET_NAME_IS_MISSING);
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

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
