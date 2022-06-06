package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONSENT_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.PERMISSION_DENIED;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_SALT;
import static com.scalar.ist.common.Constants.REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
import static com.scalar.ist.common.Constants.USER_PROFILE_ROLES;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.VALIDATE_PERMISSION;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.hash.HashCode;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.hashids.Hashids;

public class GetConsentStatus extends Contract {
  private static final JsonArray ROLES =
      Json.createArrayBuilder().add(ROLE_CONTROLLER).add(ROLE_PROCESSOR).build();

  private final String IS_HASHED_CONSENT_ID = "is_hashed_consent_id";
  private final String IS_HASHED_CONSENT_STATEMENT_ID = "is_hashed_consent_statement_id";

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);

    JsonObject getAssetArgument =
        Json.createObjectBuilder()
            .add(ASSET_ID, argument.getString(CONSENT_ID))
            .add(RECORD_IS_HASHED, argument.getBoolean(IS_HASHED_CONSENT_ID))
            .build();
    JsonObject consent = invokeSubContract(GET_ASSET_RECORD, ledger, getAssetArgument);

    if (argument.containsKey(COMPANY_ID)) {
      getAssetArgument =
          Json.createObjectBuilder()
              .add(ASSET_ID, consent.getString(CONSENT_STATEMENT_ID))
              .add(RECORD_IS_HASHED, argument.getBoolean(IS_HASHED_CONSENT_STATEMENT_ID))
              .build();
      JsonObject consentStatement = invokeSubContract(GET_ASSET_RECORD, ledger, getAssetArgument);

      if (!consentStatement.containsKey(COMPANY_ID)
          || !consentStatement.getString(COMPANY_ID).equals(argument.getString(COMPANY_ID))) {
        throw new ContractContextException(PERMISSION_DENIED);
      }
    }
    return consent;
  }

  private void validate(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArguments(ledger, argument, properties.get());
    validateUserPermissions(ledger, argument, properties.get());
  }

  private void validateProperties(Optional<JsonObject> properties) {
    if (!properties.isPresent()) {
      throw new ContractContextException(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    }
    if (!properties.get().containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
  }

  private void validateArguments(Ledger ledger, JsonObject argument, JsonObject properties) {
    JsonObject schema = properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA);
    JsonObject validateArgumentJson =
        Json.createObjectBuilder()
            .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
            .add(VALIDATE_ARGUMENT_SCHEMA, schema)
            .build();

    invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentJson);
  }

  private void validateUserPermissions(Ledger ledger, JsonObject argument, JsonObject properties) {

    if (argument.containsKey(COMPANY_ID)) {
      JsonObject userProfileArgument =
          Json.createObjectBuilder().add(COMPANY_ID, argument.getString(COMPANY_ID)).build();
      JsonObject userProfile = invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);

      JsonObject validateUserPermissionsArgument =
          Json.createObjectBuilder()
              .add(ROLES_REQUIRED, ROLES)
              .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
              .build();
      invokeSubContract(VALIDATE_PERMISSION, ledger, validateUserPermissionsArgument);
    } else {
      String assetId =
          argument.getBoolean(IS_HASHED_CONSENT_ID)
              ? decodeHashid(argument.getString(CONSENT_ID), properties.getString(RECORD_SALT))
              : argument.getString(CONSENT_ID);

      if (!assetId.endsWith(getCertificateKey().getHolderId())) {
        throw new ContractContextException(PERMISSION_DENIED);
      }
    }
  }

  public String decodeHashid(String source, String salt) {
    return new String(HashCode.fromString(new Hashids(salt).decodeHex(source)).asBytes());
  }

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
