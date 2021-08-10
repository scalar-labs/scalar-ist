package com.scalar.ist.function;

import static com.scalar.ist.common.Constants.ACTION;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.DB_TABLE_CLUSTERING_KEYS;
import static com.scalar.ist.common.Constants.DB_TABLE_COLUMNS;
import static com.scalar.ist.common.Constants.DB_TABLE_NAME;
import static com.scalar.ist.common.Constants.DB_TABLE_PARTITION_KEYS;
import static com.scalar.ist.common.Constants.DB_VALUE_BIGINT;
import static com.scalar.ist.common.Constants.DB_VALUE_BLOB;
import static com.scalar.ist.common.Constants.DB_VALUE_BOOLEAN;
import static com.scalar.ist.common.Constants.DB_VALUE_FLOAT;
import static com.scalar.ist.common.Constants.DB_VALUE_INT;
import static com.scalar.ist.common.Constants.DB_VALUE_NAME;
import static com.scalar.ist.common.Constants.DB_VALUE_TEXT;
import static com.scalar.ist.common.Constants.DB_VALUE_TYPE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.INSERT_ACTION;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.RECORD_IS_ALREADY_REGISTERED;
import static com.scalar.ist.common.Constants.RECORD_NOT_FOUND;
import static com.scalar.ist.common.Constants.TABLE_SCHEMA;
import static com.scalar.ist.common.Constants.UPDATE_ACTION;

import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.BlobValue;
import com.scalar.db.io.BooleanValue;
import com.scalar.db.io.FloatValue;
import com.scalar.db.io.IntValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.db.io.Value;
import com.scalar.dl.ledger.database.Database;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.function.Function;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.bouncycastle.util.encoders.Base64;

public class UpsertMaster extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    JsonObject benefitSchema = contractProperties.get().getJsonObject(TABLE_SCHEMA);
    String tableName = benefitSchema.getString(DB_TABLE_NAME);
    List<Value<?>> partitionKeysValues =
        toValues(
            contractProperties.get(),
            contractArgument,
            benefitSchema.getJsonArray(DB_TABLE_PARTITION_KEYS));
    List<Value<?>> clusteringKeysValues =
        toValues(
            contractProperties.get(),
            contractArgument,
            benefitSchema.getJsonArray(DB_TABLE_CLUSTERING_KEYS));

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
    put.withValues(
        toValues(
            contractProperties.get(),
            contractArgument,
            benefitSchema.getJsonArray(DB_TABLE_COLUMNS)));
    database.put(put);
  }

  List<Value<?>> toValues(
      JsonObject contractProperties, JsonObject contractArgument, JsonArray values) {
    return values.stream()
        .map(JsonValue::asJsonObject)
        .filter(
            value -> {
              String valueName = value.getString(DB_VALUE_NAME);
              return contractArgument.containsKey(valueName) || valueName.equals(CREATED_BY);
            })
        .map(
            value -> {
              String valueName = value.getString(DB_VALUE_NAME);
              String valueType = value.getString(DB_VALUE_TYPE);
              switch (valueType) {
                case DB_VALUE_BIGINT:
                  return new BigIntValue(
                      valueName, contractArgument.getJsonNumber(valueName).longValue());
                case DB_VALUE_TEXT:
                  if (valueName.equals(CREATED_BY)) {
                    return new TextValue(CREATED_BY, contractProperties.getString(HOLDER_ID));
                  }
                  String textValue;
                  JsonValue jsonValue = contractArgument.get(valueName);
                  // In case of a jsonValue == JsonString, calling jsonValue.toString() will add
                  // additional quotes around the
                  // value like this : ""foo"". Calling getString() returns the bare value "foo"
                  if (jsonValue instanceof JsonString) {
                    textValue = ((JsonString) jsonValue).getString();
                  } else {
                    textValue = jsonValue.toString();
                  }
                  return new TextValue(valueName, textValue);
                case DB_VALUE_BOOLEAN:
                  return new BooleanValue(valueName, contractArgument.getBoolean(valueName));
                case DB_VALUE_FLOAT:
                  return new FloatValue(
                      valueName,
                      contractArgument.getJsonNumber(valueName).bigDecimalValue().floatValue());
                case DB_VALUE_INT:
                  return new IntValue(valueName, contractArgument.getInt(valueName));
                case DB_VALUE_BLOB:
                  return new BlobValue(
                      valueName, Base64.decode(contractArgument.getString(valueName)));
                default:
                  throw new ContractContextException(
                      String.format("The type %s is not supported", valueType));
              }
            })
        .collect(Collectors.toList());
  }

  Get createGet(String tableName, List<Value<?>> partitionKeyValues, List<Value<?>> clusteringKeyValues) {
    Get get;
    if (clusteringKeyValues.isEmpty()) {
      get = new Get(new Key(partitionKeyValues));
    } else {
      get = new Get(new Key(partitionKeyValues), new Key(clusteringKeyValues));
    }
    get.forNamespace(NAMESPACE).forTable(tableName);
    return get;
  }

  Put createPut(String tableName, List<Value<?>> partitionKeyValues, List<Value<?>> clusteringKeyValues) {
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
