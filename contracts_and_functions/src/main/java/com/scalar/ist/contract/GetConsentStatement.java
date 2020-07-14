package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ABSTRACT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_CHANGES;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_PURPOSE_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_THIRD_PARTY_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_VERSION;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.HASHED_CONSENT_STATEMENT_ID;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class GetConsentStatement extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(ledger, argument, properties);

    JsonObject getAssetArgument =
        Json.createObjectBuilder()
            .add(ASSET_ID, argument.getString(HASHED_CONSENT_STATEMENT_ID))
            .add(RECORD_IS_HASHED, true)
            .build();
    JsonObject consentStatement = invokeSubContract(GET_ASSET_RECORD, ledger, getAssetArgument);

    JsonObjectBuilder consentStatementBuilder = Json.createObjectBuilder();

    if (!consentStatement.containsKey(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID)) {
      consentStatementBuilder.add(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID, JsonValue.NULL);
    } else {
      consentStatementBuilder.add(
          CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID,
          consentStatement.getString(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID));
    }
    if (!consentStatement.containsKey(CONSENT_STATEMENT_CHANGES)) {
      consentStatementBuilder.add(CONSENT_STATEMENT_CHANGES, JsonValue.NULL);
    } else {
      consentStatementBuilder.add(
          CONSENT_STATEMENT_CHANGES, consentStatement.getString(CONSENT_STATEMENT_CHANGES));
    }

    return consentStatementBuilder
        .add(CONSENT_STATEMENT, consentStatement.getString(CONSENT_STATEMENT))
        .add(CONSENT_STATEMENT_VERSION, consentStatement.getString(CONSENT_STATEMENT_VERSION))
        .add(CONSENT_STATEMENT_ABSTRACT, consentStatement.getString(CONSENT_STATEMENT_ABSTRACT))
        .add(
            CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS,
            consentStatement.getJsonArray(CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS))
        .add(
            CONSENT_STATEMENT_PURPOSE_IDS,
            consentStatement.getJsonArray(CONSENT_STATEMENT_PURPOSE_IDS))
        .add(
            CONSENT_STATEMENT_THIRD_PARTY_IDS,
            consentStatement.getJsonArray(CONSENT_STATEMENT_THIRD_PARTY_IDS))
        .build();
  }

  private void validate(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validateProperties(properties);
    validateArguments(ledger, argument, properties.get());
  }

  private void validateProperties(Optional<JsonObject> properties) {
    if (!properties.isPresent()) {
      throw new ContractContextException(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    }
    if (!properties.get().containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
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

  @VisibleForTesting
  JsonObject invokeSubContract(String contractId, Ledger ledger, JsonObject argument) {
    return invoke(contractId, ledger, argument);
  }
}
