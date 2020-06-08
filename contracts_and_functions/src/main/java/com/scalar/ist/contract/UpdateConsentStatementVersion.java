package com.scalar.ist.contract;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import org.json.JSONObject;

import javax.json.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.scalar.ist.common.Constants.*;

public class UpdateConsentStatementVersion extends Contract {
  private static final JsonArray ROLES = Json.createArrayBuilder().add(ROLE_CONTROLLER).build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);
    JsonObject putAssetRecordArgument = createPutAssetRecordArgument(properties.get(), argument);
    System.out.println(new JSONObject(putAssetRecordArgument.toString()).toString(4));
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
        properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA),
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
      JsonObject assetSchema, List<String> keys, JsonObject argument, JsonObjectBuilder data) {
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

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
