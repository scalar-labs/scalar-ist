package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DRAFT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_INACTIVE;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_PUBLISHED;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_REVIEWED;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_STATUS;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_NOT_MATCHED;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID_DOES_NOT_MATCH;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_UPDATE;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.STATUS_UPDATE_NOT_ALLOWED;
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
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class UpdateConsentStatementStatus extends Contract {
  private static final JsonArray ROLES = Json.createArrayBuilder().add(ROLE_CONTROLLER).build();

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    JsonObject consentStatement = validateWithConsentStatement(ledger, argument, properties);
    JsonObject putAssetRecordArgument = createPutAssetRecordArgument(argument, consentStatement);
    return invokeSubContract(PUT_ASSET_RECORD, ledger, putAssetRecordArgument);
  }

  private JsonObject createPutAssetRecordArgument(
      JsonObject argument, JsonObject consentStatement) {
    String consentStatementId = argument.getString(CONSENT_STATEMENT_ID);
    JsonNumber createdAt = argument.getJsonNumber(UPDATED_AT);

    JsonObjectBuilder recordData = Json.createObjectBuilder(consentStatement);
    recordData.add(CONSENT_STATEMENT_STATUS, argument.get(CONSENT_STATEMENT_STATUS));

    return Json.createObjectBuilder()
        .add(ASSET_ID, consentStatementId)
        .add(RECORD_DATA, recordData)
        .add(RECORD_MODE, RECORD_MODE_UPDATE)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
        .build();
  }

  private JsonObject validateWithConsentStatement(
      Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArgument(ledger, argument, properties.get());
    validateHolderId(properties.get());
    JsonObject getAssetRecordArgument =
        Json.createObjectBuilder()
            .add(ASSET_ID, argument.getString(CONSENT_STATEMENT_ID))
            .add(RECORD_IS_HASHED, false)
            .build();
    JsonObject consentStatement =
        invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    validateUserPermissions(ledger, argument, consentStatement);
    validateStatusUpdate(argument, consentStatement);

    return consentStatement;
  }

  private void validateStatusUpdate(JsonObject arguments, JsonObject consentStatement) {
    String argStatus = arguments.getString(CONSENT_STATEMENT_STATUS);
    String csStatus = consentStatement.getString(CONSENT_STATEMENT_STATUS);
    boolean draft =
        csStatus.equals(CONSENT_STATEMENT_DRAFT)
            && (!argStatus.equals(CONSENT_STATEMENT_REVIEWED)
                && !argStatus.equals(CONSENT_STATEMENT_PUBLISHED));

    boolean reviewed =
        csStatus.equals(CONSENT_STATEMENT_REVIEWED)
            && !argStatus.equals(CONSENT_STATEMENT_PUBLISHED);
    boolean published =
        csStatus.equals(CONSENT_STATEMENT_PUBLISHED)
            && !argStatus.equals(CONSENT_STATEMENT_INACTIVE);
    boolean inactive =
        csStatus.equals(CONSENT_STATEMENT_INACTIVE)
            && !argStatus.equals(CONSENT_STATEMENT_PUBLISHED);
    if (draft || reviewed || published || inactive) {
      throw new ContractContextException(STATUS_UPDATE_NOT_ALLOWED);
    }
  }

  private void validateProperties(Optional<JsonObject> properties) {
    if (!properties.isPresent() || !properties.get().containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
    if (!properties.get().containsKey(HOLDER_ID)) {
      throw new ContractContextException(HOLDER_ID_IS_MISSING);
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

  private void validateUserPermissions(
      Ledger ledger, JsonObject arguments, JsonObject consentStatement) {
    JsonObject userProfileArgument =
        Json.createObjectBuilder().add(COMPANY_ID, arguments.getString(COMPANY_ID)).build();
    JsonObject userProfile = invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);

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

    if (!arguments.getString(ORGANIZATION_ID).equals(consentStatement.getString(ORGANIZATION_ID))) {
      throw new ContractContextException(ORGANIZATION_ID_DOES_NOT_MATCH);
    }
  }

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
