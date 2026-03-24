package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ABSTRACT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_BENEFIT_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DRAFT_STATUS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_OPTIONAL_PURPOSES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_PURPOSE_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_STATUS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_THIRD_PARTY_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_TITLE;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_VERSION;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GROUP_COMPANY_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;

import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class RegisterConsentStatement extends BaseContract {
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

    JsonObjectBuilder recordData = createRecordData(keys, arguments);
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

  private JsonObjectBuilder createRecordData(List<String> keys, JsonObject argument) {
    JsonObjectBuilder data = Json.createObjectBuilder();
    for (String key : keys)
      if (argument.containsKey(key)) {
        JsonValue.ValueType argumentValueType = argument.get(key).getValueType();
        if (argumentValueType == JsonValue.ValueType.ARRAY) {
          data.add(key, argument.getJsonArray(key));
        } else if (argumentValueType == JsonValue.ValueType.OBJECT) {
          data.add(key, argument.getJsonObject(key));
        } else if (argumentValueType == JsonValue.ValueType.STRING) {
          data.add(key, argument.getString(key));
        } else if (argumentValueType == JsonValue.ValueType.NUMBER) {
          data.add(key, argument.getJsonNumber(key));
        } else {
          throw new ContractContextException("The type " + argumentValueType + " is not supported");
        }
      }
    return data;
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
}
