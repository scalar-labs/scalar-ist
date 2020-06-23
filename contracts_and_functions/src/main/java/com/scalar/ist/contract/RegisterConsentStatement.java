package com.scalar.ist.contract;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;

import javax.json.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.scalar.ist.common.Constants.*;

public class RegisterConsentStatement extends Contract {
  //  private static final String ASSET_NAME = "consent_statement";
  private static final JsonArray ROLES = Json.createArrayBuilder().add(ROLE_CONTROLLER).build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validate(ledger, arguments, properties);
    JsonObject putRecordArgument = createPutRecordArgument(arguments, properties);
    return invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
  }

  private JsonObject createPutRecordArgument(
      JsonObject arguments, Optional<JsonObject> properties) {
    JsonNumber createdAt = arguments.getJsonNumber(CREATED_AT);
    String assetId =
        String.format(
            "%s%s-%s-%s",
            properties.get().getString(ASSET_NAME),
            properties.get().getString(ASSET_VERSION, ""),
            arguments.getString(ORGANIZATION_ID),
            arguments.getJsonNumber(CREATED_AT).toString());

    ArrayList<String> keys =
        new ArrayList<>(
            Arrays.asList(
                COMPANY_ID,
                ORGANIZATION_ID,
                GROUP_COMPANY_IDS,
                CONSENT_STATEMENT_VERSION,
                CONSENT_STATEMENT_TITLE,
                CONSENT_STATEMENT_ABSTRACT,
                CONSENT_STATEMENT_PURPOSE_IDS,
                CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS,
                CONSENT_STATEMENT_BENEFIT_IDS,
                CONSENT_STATEMENT_THIRD_PARTY_IDS,
                CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES,
                CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID,
                CONSENT_STATEMENT,
                CONSENT_STATEMENT_OPTIONAL_PURPOSES));

    JsonObjectBuilder recordData =
        createRecordData(properties.get().getJsonObject(CONTRACT_ARGUMENT_SCHEMA), keys, arguments);
    recordData.add(
        CONSENT_STATEMENT_STATUS,
        arguments.getString(CONSENT_STATEMENT_STATUS, CONSENT_STATEMENT_DRAFT_STATUS));
    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, recordData)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
        .build();
  }

  private JsonObjectBuilder createRecordData(
      JsonObject assetSchema, List<String> keys, JsonObject argument) {
    JsonObjectBuilder data = Json.createObjectBuilder();
    JsonObject metadata = assetSchema.getJsonObject(PROPERTIES).asJsonObject();
    for (String key : keys) {
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
    }
    return data;
  }

  private void validate(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArguments(ledger, arguments, properties.get());
    validateHolderId(properties.get());
    JsonObject userProfileArgument =
        Json.createObjectBuilder().add(COMPANY_ID, arguments.getString(COMPANY_ID)).build();
    JsonObject userProfile = invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    validateUserPermissions(userProfile, arguments, ledger);
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

  private void validateHolderId(JsonObject properties) {
    if (!properties.getString(HOLDER_ID).equals(getCertificateKey().getHolderId())) {
      throw new ContractContextException(HOLDER_ID_IS_NOT_MATCHED);
    }
  }

  private void validateUserPermissions(
      JsonObject userProfile, JsonObject arguments, Ledger ledger) {
    JsonObject validateUserPermissionsArgument =
        Json.createObjectBuilder()
            .add(ROLES_REQUIRED, ROLES)
            .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
            .add(ORGANIZATION_IDS_REQUIRED, userProfile.getJsonArray(ORGANIZATION_IDS))
            .add(
                ORGANIZATION_IDS_ARGUMENT,
                Json.createArrayBuilder().add(arguments.getString(ORGANIZATION_ID)).build())
            .build();

    invokeSubContract(VALIDATE_PERMISSION, ledger, validateUserPermissionsArgument);
  }

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
