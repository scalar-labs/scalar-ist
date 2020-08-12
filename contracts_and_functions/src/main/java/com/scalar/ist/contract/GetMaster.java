package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_ID_IS_NOT_PERMITTED;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.COMPANY_ID_DOES_NOT_MATCH_WITH_ASSET_COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.PERMITTED_ASSET_NAMES;
import static com.scalar.ist.common.Constants.PERMITTED_ASSET_NAMES_IS_MISSING;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
import static com.scalar.ist.common.Constants.USER_PROFILE_ROLES;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.VALIDATE_PERMISSION;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class GetMaster extends Contract {

  private static final JsonArray ROLES =
      Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).add(ROLE_PROCESSOR).add(ROLE_CONTROLLER)
          .build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);

    JsonObject getAssetArgument =
        Json.createObjectBuilder()
            .add(ASSET_ID, argument.getString(ASSET_ID))
            .build();
    JsonObject asset = invokeSubContract(GET_ASSET_RECORD, ledger, getAssetArgument);

    // Compare company_id of the acquired asset with contract_argument.company_id, and return the content of the asset if they match.
    if (asset.getString(COMPANY_ID).equals(argument.getString(COMPANY_ID))) {
      throw new ContractContextException(COMPANY_ID_DOES_NOT_MATCH_WITH_ASSET_COMPANY_ID);
    }
    return asset;
  }

  private void validate(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArguments(ledger, arguments, properties.get());
    validateUserPermissions(ledger, arguments);
  }

  private void validateProperties(Optional<JsonObject> propertiesOpt) {
    JsonObject properties = propertiesOpt.get();
    if (!properties.containsKey(HOLDER_ID)) {
      throw new ContractContextException(HOLDER_ID_IS_MISSING);
    }
    if (!properties.containsKey(ASSET_NAME)) {
      throw new ContractContextException(ASSET_NAME_IS_MISSING);
    }
    if (!properties.containsKey(PERMITTED_ASSET_NAMES)) {
      throw new ContractContextException(PERMITTED_ASSET_NAMES_IS_MISSING);
    }
  }

  private void validateArguments(Ledger ledger, JsonObject arguments, JsonObject properties) {
    JsonObject schema = properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA);
    JsonArray permittedAssetNames = properties.getJsonArray(PERMITTED_ASSET_NAMES);
    String assetName = arguments.getString(ASSET_ID).substring(0, 2);
    if (!permittedAssetNames.contains(assetName)) {
      throw new ContractContextException(ASSET_ID_IS_NOT_PERMITTED);
    }
    JsonObject validateArgumentJson =
        Json.createObjectBuilder()
            .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, arguments)
            .add(VALIDATE_ARGUMENT_SCHEMA, schema)
            .build();

    invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentJson);
  }

  private void validateUserPermissions(Ledger ledger, JsonObject arguments) {
    JsonObject userProfileArgument =
        Json.createObjectBuilder()
            .add(COMPANY_ID, arguments.getString(COMPANY_ID))
            .build();
    JsonObject userProfile = invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);

    JsonObject validateUserPermissionsArgument =
        Json.createObjectBuilder()
            .add(ROLES_REQUIRED, ROLES)
            .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
            .build();

    invokeSubContract(VALIDATE_PERMISSION, ledger, validateUserPermissionsArgument);
  }

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject arguments) {
    return invoke(contractId, ledger, arguments);
  }
}
