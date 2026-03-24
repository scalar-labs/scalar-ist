package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.THIRD_PARTY_DOMAIN;
import static com.scalar.ist.common.Constants.THIRD_PARTY_ID;
import static com.scalar.ist.common.Constants.THIRD_PARTY_METADATA;
import static com.scalar.ist.common.Constants.THIRD_PARTY_NAME;

import com.scalar.dl.ledger.database.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class RegisterThirdParty extends BaseContract {
  private static final JsonArray ROLES = Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);

    return invokeSubContract(
        PUT_ASSET_RECORD, ledger, createPutRecordArgument(argument, properties.get()));
  }

  private JsonObject createPutRecordArgument(JsonObject argument, JsonObject properties) {
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId =
        String.join(
            "-", assetName, argument.getString(COMPANY_ID), argument.getString(THIRD_PARTY_DOMAIN));

    JsonObjectBuilder builder = Json.createObjectBuilder();
    if (argument.containsKey(CORPORATE_NUMBER)) {
      builder.add(CORPORATE_NUMBER, argument.getString(CORPORATE_NUMBER));
    }
    JsonObject data =
        builder
            .add(THIRD_PARTY_ID, assetId)
            .add(COMPANY_ID, argument.getString(COMPANY_ID))
            .add(THIRD_PARTY_DOMAIN, argument.getString(THIRD_PARTY_DOMAIN))
            .add(THIRD_PARTY_NAME, argument.getString(THIRD_PARTY_NAME))
            .add(THIRD_PARTY_METADATA, argument.getJsonObject(THIRD_PARTY_METADATA))
            .add(ORGANIZATIONS, argument.getJsonArray(ORGANIZATIONS))
            .build();

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_DATA, data)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, argument.getJsonNumber(CREATED_AT))
        .build();
  }

  private void validate(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArguments(ledger, argument, properties.get());
    validateHolderId(properties.get());
    JsonObject userProfile = getUserProfile(ledger, argument, properties.get());
    validateUserPermissions(ledger, userProfile, argument, ROLES);
  }
}
