package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ABSTRACT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_BENEFIT_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_CHANGES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DRAFT_STATUS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_OPTIONAL_PURPOSES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_PARENT_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_PURPOSE_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_STATUS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_THIRD_PARTY_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_TITLE;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_VERSION;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.GROUP_COMPANY_IDS;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_NOT_MATCHED;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class UpdateConsentStatementVersion extends Contract {
  private static final JsonArray ROLES = Json.createArrayBuilder().add(ROLE_CONTROLLER).build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);
    JsonObject putAssetRecordArgument = createPutAssetRecordArgument(properties.get(), argument);
    return invokeSubContract(PUT_ASSET_RECORD, ledger, putAssetRecordArgument);
  }

  private JsonObject createPutAssetRecordArgument(JsonObject properties, JsonObject argument) {
    JsonNumber createdAt = argument.getJsonNumber(CREATED_AT);
    String assetId =
        String.format(
            "%s%s-%s-%s",
            properties.getString(ASSET_NAME),
            properties.getString(ASSET_VERSION, ""),
            argument.getString(ORGANIZATION_ID),
            createdAt.toString());
    JsonObjectBuilder data = Json.createObjectBuilder();
    addParams(
        Arrays.asList(
            CONSENT_STATEMENT_PARENT_ID,
            COMPANY_ID,
            ORGANIZATION_ID,
            GROUP_COMPANY_IDS,
            CONSENT_STATEMENT_VERSION,
            CONSENT_STATEMENT_TITLE,
            CONSENT_STATEMENT_ABSTRACT,
            CONSENT_STATEMENT_CHANGES,
            CONSENT_STATEMENT_PURPOSE_IDS,
            CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS,
            CONSENT_STATEMENT_BENEFIT_IDS,
            CONSENT_STATEMENT_THIRD_PARTY_IDS,
            CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES,
            CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID,
            CONSENT_STATEMENT,
            CONSENT_STATEMENT_OPTIONAL_PURPOSES),
        argument,
        data);
    data.add(
        CONSENT_STATEMENT_STATUS,
        argument.getString(CONSENT_STATEMENT_STATUS, CONSENT_STATEMENT_DRAFT_STATUS));

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data.build())
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
        .build();
  }

  private void validate(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArgument(ledger, argument, properties.get());
    validateHolderId(properties.get());
    validateUserPermissions(ledger, argument);
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
    if (!properties.getString(HOLDER_ID).equals(getCertificateKey().getHolderId())) {
      throw new ContractContextException(HOLDER_ID_IS_NOT_MATCHED);
    }
  }

  private void validateUserPermissions(Ledger ledger, JsonObject arguments) {
    JsonObject userProfileArgument =
        Json.createObjectBuilder().add(COMPANY_ID, arguments.getString(COMPANY_ID)).build();
    JsonObject userProfile = invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    JsonObject getAssetRecordArgument =
        Json.createObjectBuilder()
            .add(ASSET_ID, arguments.getString(CONSENT_STATEMENT_PARENT_ID))
            .add(RECORD_IS_HASHED, false)
            .build();
    JsonObject consentStatement =
        invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);

    JsonObject validateUserPermissionsArgument =
        Json.createObjectBuilder()
            .add(ROLES_REQUIRED, ROLES)
            .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
            .add(ORGANIZATION_IDS_REQUIRED, userProfile.getJsonArray(ORGANIZATION_IDS))
            .add(
                ORGANIZATION_IDS_ARGUMENT,
                Json.createArrayBuilder().add(consentStatement.getString(ORGANIZATION_ID)).build())
            .build();

    invokeSubContract(VALIDATE_PERMISSION, ledger, validateUserPermissionsArgument);
  }

  private JsonObjectBuilder addParams(
      List<String> keys, JsonObject argument, JsonObjectBuilder data) {
    for (String key : keys)
      if (argument.containsKey(key)) {
        JsonValue.ValueType argumentValueType = argument.get(key).getValueType();
        if (argumentValueType == JsonValue.ValueType.ARRAY) {
          data.add(key, argument.getJsonArray(key));
        } else if (argumentValueType == JsonValue.ValueType.OBJECT) {
          data.add(key, argument.getJsonObject(key));
        } else if (argumentValueType == JsonValue.ValueType.STRING) {
          data.add(key, argument.getString(key));
        } else {
          throw new ContractContextException("The type " + argumentValueType + " is not supported");
        }
      }
    return data;
  }

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
