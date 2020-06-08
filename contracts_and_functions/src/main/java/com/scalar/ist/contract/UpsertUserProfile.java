package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ASSET_NAME;
import static com.scalar.ist.common.Constants.COMPANY_ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.EXECUTOR_COMPANY_ID;
import static com.scalar.ist.common.Constants.EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
import static com.scalar.ist.common.Constants.PERMISSION_DENIED;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.ROLE_SYSADMIN;
import static com.scalar.ist.common.Constants.ROLE_SYSOPERATOR;
import static com.scalar.ist.common.Constants.USER_PROFILE_ASSET_NAME;
import static com.scalar.ist.common.Constants.USER_PROFILE_ASSET_VERSION;
import static com.scalar.ist.common.Constants.USER_PROFILE_EXECUTOR_COMPANY_ID;
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
import java.io.StringReader;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;

public class UpsertUserProfile extends Contract {
  private static final JsonArray ROLES =
      Json.createArrayBuilder()
          .add(ROLE_ADMINISTRATOR)
          .add(ROLE_SYSOPERATOR)
          .add(ROLE_SYSADMIN)
          .build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validate(ledger, arguments, properties);
    JsonObject putRecordArgument = createPutRecordArgument(arguments, properties.get());
    return invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
  }

  private JsonObject createPutRecordArgument(JsonObject arguments, JsonObject properties) {
    JsonNumber createdAt = arguments.getJsonNumber(CREATED_AT);
    String assetName = properties.getString(USER_PROFILE_ASSET_NAME) + properties.getString(USER_PROFILE_ASSET_VERSION, "");
    String assetId =
        String.format(
            "%s-%s-%s",
            assetName, arguments.getString(COMPANY_ID), arguments.getString(HOLDER_ID));

    JsonObject recordData =
        Json.createObjectBuilder()
            .add(COMPANY_ID, arguments.getString(COMPANY_ID))
            .add(ORGANIZATION_IDS, arguments.getJsonArray(ORGANIZATION_IDS))
            .add(Constants.ROLES, arguments.getJsonArray(Constants.ROLES))
            .add(HOLDER_ID, arguments.getString(HOLDER_ID))
            .build();

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, recordData)
        .add(RECORD_MODE, arguments.getString(RECORD_MODE))
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
        .build();
  }

  private void validate(Ledger ledger, JsonObject arguments, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArguments(ledger, arguments, properties.get());
    JsonObject userProfileArgument =
        Json.createObjectBuilder()
            .add(COMPANY_ID, arguments.getString(USER_PROFILE_EXECUTOR_COMPANY_ID))
            .build();
    JsonObject userProfile = invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    String companyAssetName = properties.get().getString(COMPANY_ASSET_NAME) + properties.get().getString(COMPANY_ASSET_VERSION, "");
    String companyAssetId =
        String.format("%s-%s", companyAssetName, arguments.getString(COMPANY_ID));
    JsonObject companyAssetArgument =
        Json.createObjectBuilder()
            .add(ASSET_ID, companyAssetId)
            .add(RECORD_IS_HASHED, false)
            .build();
    JsonObject companyAsset = invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    validateCompanyIds(userProfile, arguments);
    validateUserPermissions(userProfile, companyAsset, arguments, ledger);
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
    if (!properties.get().containsKey(COMPANY_ASSET_NAME)) {
      throw new ContractContextException(ASSET_NAME_IS_MISSING);
    }
    if (!properties.get().containsKey(USER_PROFILE_ASSET_NAME)) {
      throw new ContractContextException(ASSET_NAME_IS_MISSING);
    }
  }

  private void validateCompanyIds(JsonObject userProfile, JsonObject arguments) {
    if (!(userProfile.getJsonArray(Constants.ROLES).contains(Json.createValue(ROLE_SYSADMIN))
        || userProfile
            .getJsonArray(Constants.ROLES)
            .contains(Json.createValue(ROLE_SYSOPERATOR)))) {
      if (!arguments.getString(EXECUTOR_COMPANY_ID).equals(userProfile.getString(COMPANY_ID))) {
        throw new ContractContextException(
            EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID);
      }
    }
  }

  private void validateUserPermissions(
      JsonObject userProfile, JsonObject companyAsset, JsonObject arguments, Ledger ledger) {
    JsonArrayBuilder organizationIds = Json.createArrayBuilder();
    JsonArray organizations = companyAsset.getJsonArray(ORGANIZATIONS);
    organizations.stream()
        .map(
            org ->
                Json.createReader(new StringReader(org.toString()))
                    .readObject()
                    .getString(ORGANIZATION_ID))
        .forEach(organizationIds::add);
    JsonObject validateUserPermissionsArgument =
        Json.createObjectBuilder()
            .add(ROLES_REQUIRED, ROLES)
            .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
            .add(ORGANIZATION_IDS_REQUIRED, organizationIds.build())
            .add(ORGANIZATION_IDS_ARGUMENT, arguments.getJsonArray(ORGANIZATION_IDS))
            .build();

    invokeSubContract(VALIDATE_PERMISSION, ledger, validateUserPermissionsArgument);

    JsonArray userProfileRoles = userProfile.getJsonArray(Constants.ROLES);
    JsonArray argumentsRoles = arguments.getJsonArray(Constants.ROLES);

    if (argumentsRoles.contains(Json.createValue(ROLE_ADMINISTRATOR))
        && !userProfileRoles.contains(Json.createValue(ROLE_SYSOPERATOR))
        && !userProfileRoles.contains(Json.createValue(ROLE_SYSADMIN))
        && !userProfileRoles.contains(Json.createValue(ROLE_ADMINISTRATOR))) {
      throw new ContractContextException(PERMISSION_DENIED);
    }
    if (argumentsRoles.contains(Json.createValue(ROLE_SYSOPERATOR))
        && !userProfileRoles.contains(Json.createValue(ROLE_SYSOPERATOR))
        && !userProfileRoles.contains(Json.createValue(ROLE_SYSADMIN))) {
      throw new ContractContextException(PERMISSION_DENIED);
    }
    if (argumentsRoles.contains(Json.createValue(ROLE_SYSADMIN))
        && !userProfileRoles.contains(Json.createValue(ROLE_SYSADMIN))) {
      throw new ContractContextException(PERMISSION_DENIED);
    }
  }

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
