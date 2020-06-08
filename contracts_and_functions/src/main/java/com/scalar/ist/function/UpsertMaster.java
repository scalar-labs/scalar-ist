package com.scalar.ist.function;

import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.io.*;
import com.scalar.dl.ledger.database.Database;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.function.Function;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import static com.scalar.ist.common.Constants.*;

public class UpsertMaster extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    JsonObject benefitSchema = contractProperties.get().getJsonObject(TABLE_SCHEMA);
    String tableName = benefitSchema.getString(DB_TABLE_NAME);

    List<Value> partitionKeysValues =
        toValues(contractArgument, benefitSchema.getJsonArray(DB_TABLE_PARTITION_KEYS));
    List<Value> clusteringKeysValues =
        toValues(contractArgument, benefitSchema.getJsonArray(DB_TABLE_CLUSTERING_KEYS));

    boolean isExistingBenefitRecord =
        database.get(createGet(tableName, partitionKeysValues, clusteringKeysValues)).isPresent();
    String action = contractArgument.getString(ACTION);
    if (action.equals(INSERT_ACTION) && isExistingBenefitRecord) {
      throw new ContractContextException(RECORD_IS_ALREADY_REGISTERED);
    }
    if (action.equals(UPDATE_ACTION) && !isExistingBenefitRecord) {
      throw new ContractContextException(RECORD_NOT_FOUND);
    }

    Put put = createPut(tableName, partitionKeysValues, clusteringKeysValues);
    put.withValues(toValues(contractArgument, benefitSchema.getJsonArray(DB_TABLE_COLUMNS)));
    database.put(put);
  }

  List<Value> toValues(JsonObject contractArgument, JsonArray values) {
    return values.stream()
        .map(JsonValue::asJsonObject)
        .filter(value -> contractArgument.containsKey(value.getString(DB_VALUE_NAME)))
        .map(
            value -> {
              String valueName = value.getString(DB_VALUE_NAME);
              String valueType = value.getString(DB_VALUE_TYPE);
              switch (valueType) {
                case DB_VALUE_BIGINT:
                  return new BigIntValue(
                      valueName, contractArgument.getJsonNumber(valueName).longValue());
                case DB_VALUE_TEXT:
                  return new TextValue(valueName, contractArgument.get(valueName).toString());
                case DB_VALUE_BOOLEAN:
                  return new BooleanValue(valueName, contractArgument.getBoolean(valueName));
                default:
                  throw new ContractContextException(
                      String.format("The type %s is not supported", valueType));
              }
            })
        .collect(Collectors.toList());
  }

  Get createGet(String tableName, List<Value> partitionKeyValues, List<Value> clusteringKeyValues) {
    Get get;
    if (clusteringKeyValues.isEmpty()) {
      get = new Get(new Key(partitionKeyValues));
    } else {
      get = new Get(new Key(partitionKeyValues), new Key(clusteringKeyValues));
    }
    get.forNamespace(NAMESPACE).forTable(tableName);
    return get;
  }

  Put createPut(String tableName, List<Value> partitionKeyValues, List<Value> clusteringKeyValues) {
    Put put;
    if (clusteringKeyValues.isEmpty()) {
      put = new Put(new Key(partitionKeyValues));
    } else {
      put = new Put(new Key(partitionKeyValues), new Key(clusteringKeyValues));
    }
    put.forNamespace(NAMESPACE).forTable(tableName);
    return put;
  }
}
