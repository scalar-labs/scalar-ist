package com.scalar.ist.tools;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.db.api.Get;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.api.Scanner;
import com.scalar.db.config.DatabaseConfig;
import com.scalar.db.exception.storage.ExecutionException;
import com.scalar.db.io.*;
import com.scalar.db.service.StorageModule;
import com.scalar.db.service.StorageService;
import com.scalar.dl.client.config.ClientConfig;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.model.ContractExecutionResult;

import javax.json.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.scalar.ist.tools.Constants.*;

public class Deploy {
  private ContractUtil util;
  private Properties clientProps;
  private StorageService storageService;

  public void process(JsonArray array) {

    util = new ContractUtil();
    array.forEach(
        jsonValue -> {
          JsonObject json = (JsonObject) jsonValue;
          JsonObjectBuilder builder =
              Json.createObjectBuilder().add(ACTION, json.getString(ACTION));

          try {
            switch (json.getString(ACTION)) {
              case SET_HOLDER:
                setContractProperties(json, builder);
                break;
              case REGISTER_CERT:
                util.registerCertificate();
                break;
              case REGISTER_FUNCTIONS:
                registerFunctions(json);
                break;
              case REGISTER_CONTRACT:
                registerContract(json, builder);
                break;
              case EXECUTE_CONTRACT:
                executeContract(json, builder);
                break;
              case SET_DATABASE_CONFIG:
                setDatabaseConfig(json);
                break;
              case CHECK_ASSET:
                checkAsset(json, builder);
                break;
              case CHECK_RECORD:
                checkRecord(json, builder);
                break;
              default:
                builder.add("command error", "specified invalid action");
            }
          } finally {
            System.out.println(builder.build());
          }
        });
  }

  private void registerFunctions(JsonObject json) {
    JsonArray jsonObject = json.getJsonArray(FUNCTIONS);

    jsonObject.forEach(
        jsonValue -> {
          JsonObject function = (JsonObject) jsonValue;

          util.registerFunction(
              function.getString(ID, ""),
              function.getString(BINARY_NAME, ""),
              function.getString(PATH, ""));
        });
  }

  private void registerContract(JsonObject json, JsonObjectBuilder builder) {
    JsonObject contractProperties;
    if (json.containsKey(PROPERTIES)) {
      contractProperties = createContractProperties(json.getJsonObject(PROPERTIES));
    } else {
      contractProperties = Json.createObjectBuilder().build();
    }
    builder.add(CONTRACT_ID, json.getString(ID));
    builder.add(CONTRACT_PROPERTIES, contractProperties);
    util.registerContract(
        json.getString(ID),
        json.getString(BINARY_NAME),
        json.getString(PATH),
        Optional.of(contractProperties));
  }

  private void executeContract(JsonObject json, JsonObjectBuilder builder) {
    JsonObject contractArgument;
    if (json.containsKey(CONTRACT_ARGUMENT)) {
      contractArgument = createContractArgument(json.getJsonObject(CONTRACT_ARGUMENT));
    } else {
      contractArgument = Json.createObjectBuilder().build();
    }

    try {
      builder.add(CONTRACT_ID, json.getString(ID));
      builder.add(CONTRACT_ARGUMENT, contractArgument);
      ContractExecutionResult result =
          util.executeContract(
              json.getString(ID),
              contractArgument,
              Optional.of(Json.createObjectBuilder().build()));

      if (json.containsKey(ASSERT_THAT)) {
        compare(result.getResult().get(), json.getJsonObject(ASSERT_THAT));
        if (result.getResult().isPresent()) {
          builder.add(ContractExecutionResult.class.getName(), result.getResult().get().toString());
        }
      }
    } catch (Exception e) {
      if (compare(e, json)) {
        builder.add("assertThrows", "success");
      } else {
        builder.add("assertThrows", "fail");
      }
      builder.add("caught exception", e.getClass().getName());
      builder.add("caught message", e.getMessage());
    }
  }

  private void setDatabaseConfig(JsonObject json) {
    Properties props = new Properties();
    props.put(
        DatabaseConfig.CONTACT_POINTS, json.getString(DatabaseConfig.CONTACT_POINTS, "localhost"));
    props.put(DatabaseConfig.CONTACT_PORT, json.getString(DatabaseConfig.CONTACT_PORT, "9042"));
    props.put(DatabaseConfig.USERNAME, json.getString(DatabaseConfig.USERNAME, "cassandra"));
    props.put(DatabaseConfig.PASSWORD, json.getString(DatabaseConfig.PASSWORD, "cassandra"));

    Injector injector = Guice.createInjector(new StorageModule(new DatabaseConfig(props)));
    storageService = injector.getInstance(StorageService.class);
    storageService.with(json.getString(NAMESPACE, "scalar"), json.getString(TABLE, "asset"));
  }

  private void checkAsset(JsonObject json, JsonObjectBuilder builder) {
    if (!json.containsKey(ASSET_ID)) {
      throw new RuntimeException("asset_id is required.");
    }

    JsonObject asset = readAsset(json.getString(ASSET_ID));
    JsonObject expect = createJsonObjectFromSetting(json.getJsonObject(EXPECT));

    expect.entrySet().stream()
        .filter(entry -> !entry.getKey().equals(_FUNCTIONS_))
        .forEach(
            jsonValueEntry -> {
              String key = jsonValueEntry.getKey();
              builder.add("Key", key);

              if (!asset.containsKey(key)) {
                builder.add("compare error", "Key not found in asset :" + key);
                return;
              }
              JsonValue assetValue = asset.get(key);
              JsonValue value = jsonValueEntry.getValue();
              try {
                compare(value, assetValue);
              } catch (RuntimeException e) {
                builder.add("compare error", e.getMessage());
              }
            });
  }

  private void checkRecord(JsonObject json, JsonObjectBuilder builder) {
    if (!json.containsKey(PARTITION_KEYS)) {
      throw new RuntimeException("partition key is required.");
    }

    try {
      Result record = readRecord(json);
      JsonObject expect = createJsonObjectFromSetting(json.getJsonObject(EXPECT));
      expect.entrySet().stream()
          .forEach(
              entry -> {
                builder.add("Key", entry.getKey());
                compare(record, entry);
              });
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  private void compare(Result record, Map.Entry<String, JsonValue> entry) {
    String key = entry.getKey();

    switch (entry.getValue().getValueType()) {
      case STRING:
        if (!((TextValue) record.getValue(key).get())
            .getString()
            .get()
            .equals(((JsonString) entry.getValue()).getString())) {
          throwRuntimeException(
              ((TextValue) record.getValue(key).get()).getString().get(),
              ((JsonString) entry.getValue()).getString());
        }
        break;
      case NUMBER:
        if (((BigIntValue) record.getValue(key).get()).get()
            != ((JsonNumber) entry.getValue()).longValue()) {
          throwRuntimeException(
              Long.toString(((BigIntValue) record.getValue(key).get()).get()),
              (entry.getValue()).toString());
        }
        break;
      case TRUE:
        if (!((BooleanValue) record.getValue(key).get()).get()) {
          throwRuntimeException(
              Boolean.toString(((BooleanValue) record.getValue(key).get()).get()), "true");
        }
        break;
      case FALSE:
        if (((BooleanValue) record.getValue(key).get()).get()) {
          throwRuntimeException(
              Boolean.toString(((BooleanValue) record.getValue(key).get()).get()), "false");
        }
        break;
    }
  }

  private void throwRuntimeException(String record, String json) {
    throw new RuntimeException(
        "Value is not matched. Record:[" + record + "] expect:[" + json + "]");
  }

  private void compare(JsonValue val1, JsonValue val2) {
    if (!(val1.getValueType() == val2.getValueType())) {
      throw new RuntimeException(
          "Json value type is not matched. " + val1.getValueType() + ":" + val2.getValueType());
    }

    switch (val1.getValueType()) {
      case STRING:
        if (!val1.toString().equals(val2.toString())) {
          throw new RuntimeException(
              "Json value is not matched. " + val1.toString() + ":" + val2.toString());
        }
        break;

      case OBJECT:
        JsonObject obj1 = val1.asJsonObject();

        obj1.entrySet().stream()
            .forEach(
                jsonValueEntry -> {
                  String key = jsonValueEntry.getKey();
                  JsonObject obj2 = val2.asJsonObject();
                  if (!obj2.containsKey(key)) {
                    throw new RuntimeException("Key not found in asset. key:" + key);
                  }
                  compare(jsonValueEntry.getValue(), obj2.get(key));
                });
        break;
    }
  }

  private boolean compare(Exception e, JsonObject json) {

    if (json.containsKey(ASSERT_THROWS) && json.getJsonObject(ASSERT_THROWS).containsKey(CLASS)) {

      String className = json.getJsonObject(ASSERT_THROWS).getString(CLASS);
      String message = json.getJsonObject(ASSERT_THROWS).getString(MESSAGE, "");

      if (e.getClass().getName().equals(className)
          && (message.length() > 0 && e.getMessage().equals(message))) {
        return true;
      }
    }
    return false;
  }

  private JsonObject createContractProperties(JsonObject json) {
    JsonObjectBuilder builder = Json.createObjectBuilder();

    json.forEach(
        (key, value) -> {
          switch (value.getValueType()) {
            case STRING:
              builder.add(key, ((JsonString) value).getString());
              break;

            case OBJECT:
              JsonObject object = value.asJsonObject();
              switch (object.getString(TYPE)) {
                case TYPE_FILE:
                  builder.add(key, Util.readJsonFromFile(object.getString(PATH)));
                  break;
                case ClientConfig.CERT_HOLDER_ID:
                  builder.add(key, clientProps.getProperty(ClientConfig.CERT_HOLDER_ID));
                  break;
                default:
                  throw new IllegalStateException("Unexpected type: " + object.getString(TYPE));
              }
              break;
          }
        });

    return builder.build();
  }

  private JsonObject createJsonObjectFromSetting(JsonObject setting) {
    switch (setting.getString(TYPE)) {
      case TYPE_STRING:
        return setting.getJsonObject(VALUE);
      case TYPE_FILE:
        return Util.readJsonFromFile(setting.getString(PATH));
    }
    return null;
  }

  private JsonObject createContractArgument(JsonObject json) {

    JsonObject argument = createJsonObjectFromSetting(json.getJsonObject(VALUE));
    JsonObjectBuilder builder = Json.createObjectBuilder(argument);

    if (json.containsKey(OPTIONAL)) {
      json.getJsonObject(OPTIONAL)
          .forEach(
              (key, value) -> {
                switch (value.getValueType()) {
                  case STRING:
                    builder.add(key, ((JsonString) value).getString());
                    break;
                  case NUMBER:
                    builder.add(key, ((JsonNumber) value).longValue());
                    break;
                  case OBJECT:
                    JsonObject object = value.asJsonObject();
                    switch (object.getString(TYPE)) {
                      case ClientConfig.CERT_HOLDER_ID:
                        builder.add(key, clientProps.getProperty(ClientConfig.CERT_HOLDER_ID));
                        break;
                      case NOW:
                        builder.add(key, new Date().getTime());
                        break;
                      default:
                        throw new IllegalStateException("Unexpected type: " + key);
                    }
                    break;
                  default:
                    throw new IllegalStateException("Unexpected entry: " + value.getValueType());
                }
              });
    }

    return builder.build();
  }

  private void setContractProperties(JsonObject json, JsonObjectBuilder builder) {
    clientProps = new Properties();
    JsonObject jsonObject = json.getJsonObject(CLIENT_PROPERTIES);
    clientProps.setProperty(
        ClientConfig.SERVER_HOST, jsonObject.getString(ClientConfig.SERVER_HOST, ""));
    clientProps.setProperty(
        ClientConfig.SERVER_PORT, jsonObject.getString(ClientConfig.SERVER_PORT, ""));
    clientProps.setProperty(
        ClientConfig.SERVER_PORT, jsonObject.getString(ClientConfig.SERVER_PORT, ""));
    clientProps.setProperty(
        ClientConfig.SERVER_PRIVILEGED_PORT,
        jsonObject.getString(ClientConfig.SERVER_PRIVILEGED_PORT, ""));
    clientProps.setProperty(
        ClientConfig.CLIENT_MODE, jsonObject.getString(ClientConfig.CLIENT_MODE, ""));
    clientProps.setProperty(
        ClientConfig.CERT_HOLDER_ID, jsonObject.getString(ClientConfig.CERT_HOLDER_ID, ""));
    clientProps.setProperty(
        ClientConfig.PRIVATE_KEY_PATH, jsonObject.getString(ClientConfig.PRIVATE_KEY_PATH, ""));
    clientProps.setProperty(
        ClientConfig.PRIVATE_KEY_PEM, jsonObject.getString(ClientConfig.PRIVATE_KEY_PEM, ""));
    clientProps.setProperty(
        ClientConfig.CERT_PATH, jsonObject.getString(ClientConfig.CERT_PATH, ""));
    clientProps.setProperty(ClientConfig.CERT_PEM, jsonObject.getString(ClientConfig.CERT_PEM, ""));
    clientProps.setProperty(
        ClientConfig.CERT_VERSION, jsonObject.getString(ClientConfig.CERT_VERSION, ""));
    clientProps.setProperty(
        ClientConfig.TLS_ENABLED, jsonObject.getString(ClientConfig.TLS_ENABLED, ""));
    util.setup(clientProps);
    builder.add(ClientConfig.CERT_HOLDER_ID, jsonObject.getString(ClientConfig.CERT_HOLDER_ID, ""));
  }

  private JsonObject readAsset(String assetId) {
    Scan scan =
        new Scan(new Key(new TextValue(ID, assetId)))
            .withOrdering(new Scan.Ordering(AGE, Scan.Ordering.Order.DESC));
    try {
      Scanner scanner = storageService.scan(scan);
      Result result = scanner.one().get();
      StringReader reader =
          new StringReader(((TextValue) (result.getValue(OUTPUT).get())).getString().get());
      return Json.createReader(reader).readObject();

    } catch (ExecutionException executionException) {
      executionException.printStackTrace();
      return null;
    }
  }

  private Result readRecord(JsonObject json) throws ExecutionException {

    List<Value> partitionKeysValues = toValues(json.getJsonArray(PARTITION_KEYS));
    List<Value> clusteringKeysValues = toValues(json.getJsonArray(CLUSTERING_KEYS));

    Get get;
    if (clusteringKeysValues.isEmpty()) {
      get = new Get(new Key(partitionKeysValues));
    } else {
      get = new Get(new Key(partitionKeysValues), new Key(clusteringKeysValues));
    }
    get.forNamespace(json.getString(NAMESPACE)).forTable(json.getString(TABLE));

    Optional<Result> result = null;
    try {
      result = storageService.get(get);
      if (result.isPresent()) {
        return result.get();
      }
    } catch (ExecutionException e) {
      throw new RuntimeException();
    }
    return null;
  }

  List<Value> toValues(JsonArray values) {

    return values.stream()
        .map(JsonValue::asJsonObject)
        .map(
            object -> {
              switch (object.getString(TYPE)) {
                case BIGINT:
                  return new BigIntValue(
                      object.getString(NAME), object.getJsonNumber(VALUE).longValue());
                case TEXT:
                  return new TextValue(object.getString(NAME), object.getString(VALUE));
                case BOOLEAN:
                  return new BooleanValue(object.getString(NAME), object.getBoolean(VALUE));
                case FLOAT:
                  return new FloatValue(
                      object.getString(NAME),
                      object.getJsonNumber(VALUE).bigDecimalValue().floatValue());
                case INT:
                  return new IntValue(object.getString(NAME), object.getInt(VALUE));
                default:
                  throw new ContractContextException(
                      String.format("The type %s is not supported", object.getString(NAME)));
              }
            })
        .collect(Collectors.toList());
  }
}
