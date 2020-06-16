package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ADMIN;
import static com.scalar.ist.common.Constants.ADMINISTRATOR_ORGANIZATION;
import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.COMPANY_ASSET_NAME;
import static com.scalar.ist.common.Constants.COMPANY_ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.COMPANY_METADATA;
import static com.scalar.ist.common.Constants.COMPANY_NAME;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.EXECUTION_RESTRICTED_TO_INITIALIZER_ACCOUNT;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_SYSADMIN;
import static com.scalar.ist.common.Constants.HOLDER_ID_SYSOPERATOR;
import static com.scalar.ist.common.Constants.INITIALIZER_ACCOUNT_NAME;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.ORGANIZATION_DESCRIPTION;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_NAME;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLE_SYSADMIN;
import static com.scalar.ist.common.Constants.ROLE_SYSOPERATOR;
import static com.scalar.ist.common.Constants.USER_PROFILE_ASSET_NAME;
import static com.scalar.ist.common.Constants.USER_PROFILE_ASSET_VERSION;
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

public class Initialize extends Contract {
  private static final String ASSET_NAME_USER_PROFILE = "user_profile";

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validate(ledger, arguments, properties);

    JsonObject putRecordArgument = createPutRecordArgument(arguments, properties.get());
    JsonObject putRecordArgumentSysAdmin =
        createPutRecordUserProfileArgument(
            arguments, properties.get(), ROLE_SYSADMIN, arguments.getString(HOLDER_ID_SYSADMIN));
    JsonObject putRecordArgumentOpAdmin =
        createPutRecordUserProfileArgument(
            arguments,
            properties.get(),
            ROLE_SYSOPERATOR,
            arguments.getString(HOLDER_ID_SYSOPERATOR));

    invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgumentSysAdmin);
    invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgumentOpAdmin);

    return null;
  }

  private JsonObject createPutRecordUserProfileArgument(
      JsonObject arguments, JsonObject properties, String role, String holderId) {
    String assetName =
        properties.getString(USER_PROFILE_ASSET_NAME)
            + properties.getString(USER_PROFILE_ASSET_VERSION, "");
    String assetId =
        String.format("%s-%s-%s", assetName, arguments.getString(COMPANY_ID), holderId);

    JsonObject data =
        Json.createObjectBuilder()
            .add(COMPANY_ID, arguments.getString(COMPANY_ID))
            .add(
                ORGANIZATION_IDS,
                Json.createArrayBuilder().add(arguments.getString(ORGANIZATION_ID)).build())
            .add(HOLDER_ID, holderId)
            .add(ROLES, Json.createArrayBuilder().add(role).build())
            .build();

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, arguments.getJsonNumber(CREATED_AT))
        .build();
  }

  private JsonObject createPutRecordArgument(JsonObject arguments, JsonObject properties) {
    String assetName =
        properties.getString(COMPANY_ASSET_NAME) + properties.getString(COMPANY_ASSET_VERSION, "");
    String assetId = String.format("%s-%s", assetName, arguments.getString(COMPANY_ID));

    JsonObject organizations =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, arguments.getString(ORGANIZATION_ID))
            .add(ORGANIZATION_NAME, ADMIN)
            .add(ORGANIZATION_DESCRIPTION, ADMINISTRATOR_ORGANIZATION)
            .add(IS_ACTIVE, true)
            .build();

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
        .add(CREATED_AT, arguments.getJsonNumber(CREATED_AT))
        .build();
  }

  private void validate(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArgument(ledger, arguments, properties.get());
    validateHolderId(properties.get());
  }

  private void validateProperties(Optional<JsonObject> properties) {
    if (!properties.isPresent() || !properties.get().containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
    if (!properties.get().containsKey(HOLDER_ID)) {
      throw new ContractContextException(HOLDER_ID_IS_MISSING);
    }
    if (!properties.get().containsKey(COMPANY_ASSET_NAME)) {
      throw new ContractContextException(ASSET_NAME_IS_MISSING);
    }
    if (!properties.get().containsKey(USER_PROFILE_ASSET_NAME)) {
      throw new ContractContextException(ASSET_NAME_IS_MISSING);
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

  private void validateHolderId(JsonObject properties) {
    String propHolderId = properties.getString(HOLDER_ID);
    if (!(propHolderId.equals(INITIALIZER_ACCOUNT_NAME)
        && getCertificateKey().getHolderId().equals(INITIALIZER_ACCOUNT_NAME))) {
      throw new ContractContextException(EXECUTION_RESTRICTED_TO_INITIALIZER_ACCOUNT);
    }
  }

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject arguments) {
    return invoke(contractId, ledger, arguments);
  }
}
