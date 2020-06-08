package com.scalar.ist.function;

import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.db.io.Value;
import com.scalar.dl.ledger.database.Database;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.function.Function;

import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.scalar.ist.common.Constants.*;

public class UpdateConsentStatementRevision extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    String consentStatementId = contractArgument.getString(CONSENT_STATEMENT_ID);
    Optional<Result> optResult = get(database, consentStatementId);
    String rootConsentStatementId = getRootConsentStatementId(optResult);
    String companyId = contractArgument.getString(COMPANY_ID);
    String organizationId = contractArgument.getString(ORGANIZATION_ID);
    String version = contractArgument.getString(CONSENT_STATEMENT_VERSION);
    String holderId = contractProperties.get().getString(HOLDER_ID);
    long createdAt = getConsentStatementCreatedAt(optResult);

    List<String> params =
        new ArrayList<>(
            Arrays.asList(
                GROUP_COMPANY_IDS,
                CONSENT_STATEMENT_CHANGES,
                CONSENT_STATEMENT_ABSTRACT,
                CONSENT_STATEMENT_TITLE,
                CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS,
                CONSENT_STATEMENT_PURPOSE_IDS,
                CONSENT_STATEMENT_BENEFIT_IDS,
                CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID,
                CONSENT_STATEMENT_THIRD_PARTY_IDS,
                CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES,
                CONSENT_STATEMENT,
                CONSENT_STATEMENT_OPTIONAL_PURPOSES,
                UPDATED_AT));
    String status =
        contractArgument.getString(CONSENT_STATEMENT_STATUS, CONSENT_STATEMENT_DRAFT_STATUS);

    Key partitionKey =
        new Key(new TextValue(CONSENT_STATEMENT_ROOT_CONSENT_STATEMENT_ID, rootConsentStatementId));
    Key clusteringKey =
        new Key(
            new TextValue(COMPANY_ID, companyId),
            new TextValue(ORGANIZATION_ID, organizationId),
            new TextValue(CONSENT_STATEMENT_ID, consentStatementId),
            new TextValue(CONSENT_STATEMENT_VERSION, version));

    Get get =
        new Get(partitionKey, clusteringKey)
            .forNamespace(NAMESPACE)
            .forTable(CONSENT_STATEMENT_TABLE);
    if (!database.get(get).isPresent()) {
      throw new ContractContextException(RECORD_NOT_FOUND);
    }

    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(new TextValue(CREATED_BY, holderId))
            .withValue(new TextValue(CONSENT_STATEMENT_STATUS, status))
            .withValue(new BigIntValue(CREATED_AT, createdAt))
            .withValues(
                createValues(
                    contractProperties.get().getJsonObject(CONTRACT_ARGUMENT_SCHEMA),
                    params,
                    contractArgument))
            .forNamespace(NAMESPACE)
            .forTable(CONSENT_STATEMENT_TABLE);

    database.put(put);
  }

  private List<Value> createValues(
      JsonObject assetSchema, List<String> keys, JsonObject contractArgument) {
    List<Value> values = new ArrayList<>();
    JsonObject metadata = assetSchema.getJsonObject(PROPERTIES).asJsonObject();
    for (String key : keys)
      if (contractArgument.containsKey(key) && metadata.containsKey(key)) {
        switch (metadata.getJsonObject(key).asJsonObject().getString(ASSET_TYPE)) {
          case ASSET_ARRAY_TYPE:
            values.add(new TextValue(key, contractArgument.getJsonArray(key).toString()));
            break;
          case ASSET_OBJECT_TYPE:
            values.add(new TextValue(key, contractArgument.getJsonObject(key).toString()));
            break;
          case ASSET_STRING_TYPE:
            values.add(new TextValue(key, contractArgument.getString(key)));
            break;
          case ASSET_INTEGER_TYPE:
            values.add(new BigIntValue(key, contractArgument.getJsonNumber(key).longValue()));
            break;
          default:
            throw new ContractContextException(
                "The type " + contractArgument.get(key).getValueType() + " is not supported");
        }
      }
    return values;
  }

  private long getConsentStatementCreatedAt(Optional<Result> optResult) {
    if (optResult.isPresent()) {
      return ((BigIntValue)
              optResult
                  .get()
                  .getValue(CREATED_AT)
                  .orElseThrow(() -> new ContractContextException("Created At not found")))
          .get();
    } else {
      throw new ContractContextException("Consent Statement not found in the database");
    }
  }

  private String getRootConsentStatementId(Optional<Result> optResult) {
    if (optResult.isPresent()) {
      return ((TextValue)
              optResult.get().getValue(CONSENT_STATEMENT_ROOT_CONSENT_STATEMENT_ID).get())
          .getString()
          .orElseThrow(() -> new ContractContextException("Root consent statement id not found"));
    } else {
      throw new ContractContextException("Consent Statement not found in the database");
    }
  }

  private Optional<Result> get(Database database, String consentStatementId) {
    Get get =
        new Get(new Key(new TextValue(CONSENT_STATEMENT_ID, consentStatementId)))
            .forNamespace(NAMESPACE)
            .forTable(CONSENT_STATEMENT_TABLE);
    return database.get(get);
  }
}
