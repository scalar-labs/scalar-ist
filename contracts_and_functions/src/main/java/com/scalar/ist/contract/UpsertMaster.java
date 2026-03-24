package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ACTION;
import static com.scalar.ist.common.Constants.ASSET_ARRAY_TYPE;
import static com.scalar.ist.common.Constants.ASSET_BOOLEAN_TYPE;
import static com.scalar.ist.common.Constants.ASSET_DEFAULT_VALUE;
import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_INTEGER_TYPE;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_NUMBER_TYPE;
import static com.scalar.ist.common.Constants.ASSET_OBJECT_TYPE;
import static com.scalar.ist.common.Constants.ASSET_SCHEMA;
import static com.scalar.ist.common.Constants.ASSET_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_STRING_TYPE;
import static com.scalar.ist.common.Constants.ASSET_TYPE;
import static com.scalar.ist.common.Constants.ASSET_TYPE_PATTERN;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.ASSET_SCHEMA;
import static com.scalar.ist.common.Constants.ASSET_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.INSERT_ACTION;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.PROPERTIES;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
import static com.scalar.ist.common.Constants.TABLE_SCHEMA;
import static com.scalar.ist.common.Constants.TABLE_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.UPDATED_AT;
import static com.scalar.ist.common.Constants.UPDATE_ACTION;

import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import java.util.Map;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class UpsertMaster extends BaseContract {
  private static final JsonArray ROLES =
      Json.createArrayBuilder().add(ROLE_PROCESSOR).add(ROLE_CONTROLLER).build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validate(ledger, arguments, properties);
    JsonObject putRecordArgument = createPutRecordArgument(ledger, properties.get(), arguments);
    return invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
  }

  private JsonObject createPutRecordArgument(
      Ledger ledger, JsonObject properties, JsonObject argument) {
    JsonNumber createdAt = argument.getJsonNumber(CREATED_AT);
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId =
        String.join("-", assetName, argument.getString(ORGANIZATION_ID), createdAt.toString());

    JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
    String action = argument.getString(ACTION);
    if (action.equals(UPDATE_ACTION)) {
      JsonObject benefit = getBenefit(ledger, assetId);
      dataBuilder = Json.createObjectBuilder(benefit);
    }
    addData(assetId, properties.getJsonObject(ASSET_SCHEMA), argument, dataBuilder);

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, dataBuilder.build())
        .add(RECORD_MODE, action)
        .add(RECORD_IS_HASHED, false)
        .add(
            CREATED_AT,
            action.equals(INSERT_ACTION) ? createdAt : argument.getJsonNumber(UPDATED_AT))
        .build();
  }

  private void validate(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArguments(ledger, arguments, properties.get());
    validateHolderId(properties.get());
    JsonObject userProfile = getUserProfile(ledger, arguments, properties.get());
    JsonArray targetOrganizationIds =
        Json.createArrayBuilder().add(arguments.getString(ORGANIZATION_ID)).build();
    validateUserPermissions(ledger, userProfile, arguments, ROLES, targetOrganizationIds);
  }

  @Override
  protected void validateProperties(Optional<JsonObject> properties) {
    super.validateProperties(properties);
    JsonObject props = properties.get();
    if (!props.containsKey(ASSET_SCHEMA)) {
      throw new ContractContextException(ASSET_SCHEMA_IS_MISSING);
    }
    if (!props.containsKey(TABLE_SCHEMA)) {
      throw new ContractContextException(TABLE_SCHEMA_IS_MISSING);
    }
  }

  private void addData(
      String assetId, JsonObject assetSchema, JsonObject argument, JsonObjectBuilder data) {
    for (Map.Entry<String, JsonValue> argumentMetadata :
        assetSchema.getJsonObject(PROPERTIES).entrySet()) {
      JsonObject metadata = argumentMetadata.getValue().asJsonObject();
      switch (metadata.getString(ASSET_TYPE)) {
        case ASSET_STRING_TYPE:
          if (ASSET_ID.equals(metadata.getString(ASSET_TYPE_PATTERN, ""))) {
            data.add(argumentMetadata.getKey(), assetId);
            break;
          }
          if (argument.containsKey(argumentMetadata.getKey())) {
            data.add(argumentMetadata.getKey(), argument.getString(argumentMetadata.getKey()));
          } else if (metadata.containsKey(ASSET_DEFAULT_VALUE)) {
            data.add(argumentMetadata.getKey(), metadata.getString(ASSET_DEFAULT_VALUE));
          }
          break;
        case ASSET_BOOLEAN_TYPE:
          if (argument.containsKey(argumentMetadata.getKey())) {
            data.add(argumentMetadata.getKey(), argument.getBoolean(argumentMetadata.getKey()));
          } else if (metadata.containsKey(ASSET_DEFAULT_VALUE)) {
            data.add(argumentMetadata.getKey(), metadata.getBoolean(ASSET_DEFAULT_VALUE));
          }
          break;
        case ASSET_ARRAY_TYPE:
          if (argument.containsKey(argumentMetadata.getKey())) {
            data.add(argumentMetadata.getKey(), argument.getJsonArray(argumentMetadata.getKey()));
          } else if (metadata.containsKey(ASSET_DEFAULT_VALUE)) {
            data.add(argumentMetadata.getKey(), metadata.getJsonArray(ASSET_DEFAULT_VALUE));
          }
          break;
        case ASSET_OBJECT_TYPE:
          if (argument.containsKey(argumentMetadata.getKey())) {
            data.add(argumentMetadata.getKey(), argument.getJsonObject(argumentMetadata.getKey()));
          } else if (metadata.containsKey(ASSET_DEFAULT_VALUE)) {
            data.add(argumentMetadata.getKey(), metadata.getJsonObject(ASSET_DEFAULT_VALUE));
          }
          break;
        case ASSET_NUMBER_TYPE:
          if (argument.containsKey(argumentMetadata.getKey())) {
            data.add(argumentMetadata.getKey(), argument.getJsonNumber(argumentMetadata.getKey()));
          } else if (metadata.containsKey(ASSET_DEFAULT_VALUE)) {
            data.add(argumentMetadata.getKey(), metadata.getJsonNumber(ASSET_DEFAULT_VALUE));
          }
          break;
        case ASSET_INTEGER_TYPE:
          if (argument.containsKey(argumentMetadata.getKey())) {
            data.add(argumentMetadata.getKey(), argument.getInt(argumentMetadata.getKey()));
          } else if (metadata.containsKey(ASSET_DEFAULT_VALUE)) {
            data.add(argumentMetadata.getKey(), metadata.getInt(ASSET_DEFAULT_VALUE));
          }
          break;
        default:
          throw new ContractContextException(
              "The type " + argument.get(argumentMetadata).getValueType() + " is not supported");
      }
    }
  }

  private JsonObject getBenefit(Ledger ledger, String assetId) {
    JsonObject getAssetRecordArgument =
        Json.createObjectBuilder().add(ASSET_ID, assetId).add(RECORD_IS_HASHED, false).build();
    return invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
  }

}
