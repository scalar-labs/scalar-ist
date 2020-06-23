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

public class UpsertConsentStatus extends Function {
  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    String dataSubjectId = contractProperties.get().getString(HOLDER_ID);
    String consentStatementId = contractArgument.getString(CONSENT_STATEMENT_ID);

    List<String> params =
        new ArrayList<>(
            Arrays.asList(CONSENT_STATUS, CONSENT_ID, CONSENTED_DETAIL, REJECTED_DETAIL));

    Key partitionKey = new Key(new TextValue(DATA_SUBJECT_ID, dataSubjectId));
    Key clusteringKey = new Key(new TextValue(CONSENT_STATEMENT_ID, consentStatementId));

    Get get = new Get(partitionKey, clusteringKey).forNamespace(NAMESPACE).forTable(CONSENT_TABLE);
    database.get(get);

    Put put =
        new Put(partitionKey, clusteringKey)
            .withValues(
                createValues(
                    contractProperties.get().getJsonObject(CONTRACT_ARGUMENT_SCHEMA),
                    params,
                    contractArgument))
            .forTable(CONSENT_TABLE)
            .forNamespace(NAMESPACE);
    Optional<Result> consent = get(database, consentStatementId);
    if (!consent.isPresent()) {
      put.withValue(
          new BigIntValue(CREATED_AT, contractArgument.getJsonNumber(UPDATED_AT).longValue()));
    }

    database.put(put);
  }

  private Optional<Result> get(Database database, String consentStatementId) {
    Get get =
        new Get(new Key(new TextValue(CONSENT_STATEMENT_ID, consentStatementId)))
            .forNamespace(NAMESPACE)
            .forTable(CONSENT_TABLE);
    return database.get(get);
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
}
