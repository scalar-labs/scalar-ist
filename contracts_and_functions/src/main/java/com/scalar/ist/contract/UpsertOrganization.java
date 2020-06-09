package com.scalar.ist.contract;

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
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
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
import static com.scalar.ist.common.Constants.RECORD_MODE_UPSERT;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.UPDATED_AT;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class UpsertOrganization extends Contract {
  private static final JsonArray ROLES = Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);

    return invokeSubContract(PUT_ASSET_RECORD, ledger, createPutRecordArgument(ledger, argument, properties.get()));
  }

  private JsonObject createPutRecordArgument(Ledger ledger, JsonObject argument, JsonObject properties) {
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId = String.join("-", assetName, argument.getString(COMPANY_ID));
    JsonObject company = getCompany(ledger, assetId);

    JsonObject updatedOrganization =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, argument.getString(ORGANIZATION_ID))
            .add(ORGANIZATION_NAME, argument.getString(ORGANIZATION_NAME))
            .add(ORGANIZATION_DESCRIPTION, argument.getString(ORGANIZATION_DESCRIPTION))
            .add(IS_ACTIVE, argument.getBoolean(IS_ACTIVE))
            .build();

    List<JsonObject> organizations = new ArrayList<>();
    boolean orgExists = false;
    for (JsonObject currentOrganization :
        company.getJsonArray(ORGANIZATIONS).getValuesAs(JsonValue::asJsonObject)) {
      if (currentOrganization
          .getString(ORGANIZATION_ID)
          .equals(argument.getString(ORGANIZATION_ID))) {
        currentOrganization = updatedOrganization;
        orgExists = true;
      }
      organizations.add(currentOrganization);
    }
    if (!orgExists) {
      organizations.add(updatedOrganization);
    }

    JsonArrayBuilder organizationsBuilder = Json.createArrayBuilder();
    for (JsonObject org : organizations) {
      organizationsBuilder.add(org);
    }
    JsonObject data = createDataObject(organizationsBuilder.build(), argument, company);

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_MODE, RECORD_MODE_UPSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, argument.getJsonNumber(UPDATED_AT))
        .build();
  }

  private JsonObject getCompany(Ledger ledger, String assetId) {
    JsonObject getAssetRecordArgument =
        Json.createObjectBuilder().add(ASSET_ID, assetId).add(RECORD_IS_HASHED, false).build();
    return invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
  }

  private JsonObject createDataObject(
      JsonArray organizations, JsonObject argument, JsonObject company) {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, argument.getString(COMPANY_ID))
        .add(COMPANY_NAME, company.getString(COMPANY_NAME))
        .add(CORPORATE_NUMBER, company.getString(CORPORATE_NUMBER))
        .add(COMPANY_METADATA, company.getJsonObject(COMPANY_METADATA))
        .add(ORGANIZATIONS, organizations)
        .build();
  }

  private void validate(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArgument(ledger, argument, properties.get());
    validateHolderId(properties.get().getString(HOLDER_ID));
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

  private void validateHolderId(String holderId) {
    if (!holderId.equals(getCertificateKey().getHolderId())) {
      throw new ContractContextException(HOLDER_ID_IS_NOT_MATCHED);
    }
  }

  private void validateUserPermissions(Ledger ledger, JsonObject argument) {
    JsonObject userProfileArgument =
        Json.createObjectBuilder().add(COMPANY_ID, argument.getString(COMPANY_ID)).build();
    JsonObject userProfile = invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);

    JsonObject validateUserPermissionsArgument =
        Json.createObjectBuilder()
            .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
            .add(ROLES_REQUIRED, ROLES)
            .build();

    invokeSubContract(VALIDATE_PERMISSION, ledger, validateUserPermissionsArgument);
  }

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
