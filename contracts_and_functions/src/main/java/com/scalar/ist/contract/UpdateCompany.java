package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.COMPANY_METADATA;
import static com.scalar.ist.common.Constants.COMPANY_NAME;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.EXECUTOR_COMPANY_ID;
import static com.scalar.ist.common.Constants.EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_UPSERT;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.ROLE_SYSOPERATOR;
import static com.scalar.ist.common.Constants.UPDATED_AT;

import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class UpdateCompany extends BaseContract {
  private static final JsonArray ROLES =
      Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).add(ROLE_SYSOPERATOR).build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validate(ledger, arguments, properties);
    JsonObject putRecordArgument = createPutRecordArgument(ledger, arguments, properties.get());
    return invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
  }

  private JsonObject createPutRecordArgument(
      Ledger ledger, JsonObject arguments, JsonObject properties) {
    JsonNumber updatedAt = arguments.getJsonNumber(UPDATED_AT);
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId = String.format("%s-%s", assetName, arguments.getString(COMPANY_ID));
    JsonObject company = getCompanyAsset(ledger, assetId);

    JsonObjectBuilder data =
        Json.createObjectBuilder()
            .add(COMPANY_ID, arguments.getString(COMPANY_ID))
            .add(COMPANY_NAME, arguments.getString(COMPANY_NAME))
            .add(COMPANY_METADATA, arguments.getJsonObject(COMPANY_METADATA))
            .add(ORGANIZATIONS, company.get(ORGANIZATIONS));
    if (arguments.containsKey(CORPORATE_NUMBER)) {
      data.add(CORPORATE_NUMBER, arguments.getString(CORPORATE_NUMBER));
    }

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_MODE, RECORD_MODE_UPSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, updatedAt)
        .build();
  }

  private JsonObject getCompanyAsset(Ledger ledger, String assetId) {
    JsonObject getAssetRecordArgument =
        Json.createObjectBuilder().add(ASSET_ID, assetId).add(RECORD_IS_HASHED, false).build();
    return invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
  }

  private void validate(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArguments(ledger, argument, properties.get());
    validateHolderId(properties.get());
    validateUserPermissions(ledger, argument);
  }

  private void validateUserPermissions(Ledger ledger, JsonObject arguments) {
    JsonObject userProfile = getUserProfile(ledger, arguments, null, EXECUTOR_COMPANY_ID);

    JsonArray adminRole = Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build();
    if (userProfile.getJsonArray(Constants.ROLES).equals(adminRole)
        && !arguments.getString(EXECUTOR_COMPANY_ID).equals(arguments.getString(COMPANY_ID))) {
      throw new ContractContextException(
          EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID);
    }

    validateUserPermissions(ledger, userProfile, arguments, ROLES);
  }
}
