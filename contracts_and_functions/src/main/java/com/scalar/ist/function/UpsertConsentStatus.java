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
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.CONSENTED_DETAIL;
import static com.scalar.ist.common.Constants.CONSENT_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATUS;
import static com.scalar.ist.common.Constants.CONSENT_TABLE;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.DATA_SUBJECT_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.REJECTED_DETAIL;
import static com.scalar.ist.common.Constants.UPDATED_AT;

public class UpsertConsentStatus extends Function {
  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    String dataSubjectId = contractProperties.get().getString(HOLDER_ID);
    String consentStatementId = contractArgument.getString(CONSENT_STATEMENT_ID);
    String consentId =
        String.format(
            "%s%s-%s-%s",
            contractProperties.get().getString(ASSET_NAME),
            contractProperties.get().getString(ASSET_VERSION, ""),
            consentStatementId,
            dataSubjectId);
    List<String> params =
        new ArrayList<>(
            Arrays.asList(CONSENT_STATUS, CONSENTED_DETAIL, REJECTED_DETAIL, UPDATED_AT));

    Key partitionKey = new Key(new TextValue(DATA_SUBJECT_ID, dataSubjectId));
    Key clusteringKey = new Key(new TextValue(CONSENT_STATEMENT_ID, consentStatementId));

    Optional<Result> optConsent = get(database, partitionKey, clusteringKey);

    List<Value<?>> values = createValues(params, contractArgument);
    values.add(new TextValue(CONSENT_ID, consentId));
    if (!optConsent.isPresent()) {
      values.add(
          new BigIntValue(CREATED_AT, contractArgument.getJsonNumber(UPDATED_AT).longValue()));
    }
    Put put =
        new Put(partitionKey, clusteringKey)
            .withValues(values)
            .forTable(CONSENT_TABLE)
            .forNamespace(NAMESPACE);

    database.put(put);
  }

  private Optional<Result> get(Database database, Key partitionKey, Key clusteringKey) {
    Get get = new Get(partitionKey, clusteringKey).forNamespace(NAMESPACE).forTable(CONSENT_TABLE);
    return database.get(get);
  }

  private List<Value<?>> createValues(List<String> keys, JsonObject contractArgument) {
    List<Value<?>> values = new ArrayList<>();
    for (String key : keys)
      if (contractArgument.containsKey(key)) {
        JsonValue.ValueType argumentValueType = contractArgument.get(key).getValueType();
        if (argumentValueType == JsonValue.ValueType.ARRAY) {
          values.add(new TextValue(key, contractArgument.getJsonArray(key).toString()));
        } else if (argumentValueType == JsonValue.ValueType.OBJECT) {
          values.add(new TextValue(key, contractArgument.getJsonObject(key).toString()));
        } else if (argumentValueType == JsonValue.ValueType.STRING) {
          values.add(new TextValue(key, contractArgument.getString(key)));
        } else if (argumentValueType == JsonValue.ValueType.NUMBER) {
          values.add(new BigIntValue(key, contractArgument.getJsonNumber(key).longValue()));
        } else {
          throw new ContractContextException("The type " + argumentValueType + " is not supported");
        }
      }
    return values;
  }
}
