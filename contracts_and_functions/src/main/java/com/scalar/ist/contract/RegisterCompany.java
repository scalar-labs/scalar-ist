package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ADMIN;
import static com.scalar.ist.common.Constants.ADMINISTRATOR_ORGANIZATION;
import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.COMPANY_METADATA;
import static com.scalar.ist.common.Constants.COMPANY_NAME;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.EXECUTOR_COMPANY_ID;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_NOT_MATCHED;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.ORGANIZATION_DESCRIPTION;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_NAME;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_SYSADMIN;
import static com.scalar.ist.common.Constants.ROLE_SYSOPERATOR;
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
import javax.json.JsonNumber;
import javax.json.JsonObject;

public class RegisterCompany extends Contract {
  private static final JsonArray ROLES =
      Json.createArrayBuilder().add(ROLE_SYSADMIN).add(ROLE_SYSOPERATOR).build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validate(ledger, arguments, properties);
    JsonObject putRecordArgument = createPutRecordArgument(arguments, properties.get());
    return invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
  }

  private JsonObject createPutRecordArgument(JsonObject arguments, JsonObject properties) {
    JsonNumber createdAt = arguments.getJsonNumber(CREATED_AT);
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId = String.format("%s-%s", assetName, arguments.getString(COMPANY_ID));

    JsonObject administratorOrganization =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, arguments.getString(ORGANIZATION_ID))
            .add(ORGANIZATION_NAME, ADMIN)
            .add(ORGANIZATION_DESCRIPTION, ADMINISTRATOR_ORGANIZATION)
            .add(IS_ACTIVE, true)
            .build();
    JsonArray organizations = Json.createArrayBuilder().add(administratorOrganization).build();
    JsonObject data =
        Json.createObjectBuilder()
            .add(COMPANY_ID, arguments.getString(COMPANY_ID))
            .add(COMPANY_NAME, arguments.getString(COMPANY_NAME))
            .add(CORPORATE_NUMBER, arguments.getString(CORPORATE_NUMBER))
            .add(COMPANY_METADATA, arguments.getJsonObject(COMPANY_METADATA))
            .add(ORGANIZATIONS, organizations)
            .build();

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
        .build();
  }

  private void validate(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArguments(ledger, arguments, properties.get());
    validateHolderId(properties.get());
    validateUserPermissions(ledger, arguments);
  }

  private void validateProperties(Optional<JsonObject> properties) {
    if (!properties.isPresent() || !properties.get().containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
    if (!properties.get().containsKey(HOLDER_ID)) {
      throw new ContractContextException(HOLDER_ID_IS_MISSING);
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

  private void validateHolderId(JsonObject properties) {
    if (!properties.getString(HOLDER_ID).equals(getCertificateKey().getHolderId())) {
      throw new ContractContextException(HOLDER_ID_IS_NOT_MATCHED);
    }
  }

  private void validateUserPermissions(Ledger ledger, JsonObject arguments) {
    JsonObject userProfileArgument =
        Json.createObjectBuilder()
            .add(COMPANY_ID, arguments.getString(EXECUTOR_COMPANY_ID))
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
