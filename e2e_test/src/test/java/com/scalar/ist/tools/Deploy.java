package com.scalar.ist.tools;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.api.Scanner;
import com.scalar.db.config.DatabaseConfig;
import com.scalar.db.exception.storage.ExecutionException;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.db.service.StorageModule;
import com.scalar.db.service.StorageService;
import com.scalar.dl.client.config.ClientConfig;
import com.scalar.dl.ledger.model.ContractExecutionResult;

import javax.json.*;
import java.io.*;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

import static com.scalar.ist.tools.Constants.*;

public class Deploy {
  private ContractUtil util;
  private Properties clientProperties;
  private StorageService storageService;

  public void process(JsonArray array) {

    util = new ContractUtil();
    array.forEach(
        jsonValue -> {
          JsonObject json = (JsonObject) jsonValue;
          System.out.println(
              Json.createObjectBuilder().add(ACTION, json.getString(ACTION)).build());
          switch (json.getString(ACTION)) {
            case SET_HOLDER:
              setContractProperties(json);
              return;
            case REGISTER_CERT:
              util.registerCertificate();
              return;
            case REGISTER_FUNCTIONS:
              registerFunctions(json);
              return;
            case REGISTER_CONTRACT:
              registerContract(json);
              return;
            case EXECUTE_CONTRACT:
              executeContract(json);
              return;
            case SET_DATABASE_CONFIG:
              setDatabaseConfig(json);
              return;
            case CHECK_ASSET:
              checkAsset(json);
              return;
            default:
              System.out.println("INVALID ACTION");
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

  private void registerContract(JsonObject json) {
    JsonObject contractProperties;
    if (json.containsKey(PROPERTIES)) {
      contractProperties = createContractProperties(json.getJsonObject(PROPERTIES));
    } else {
      contractProperties = Json.createObjectBuilder().build();
    }
    util.registerContract(
        json.getString(ID),
        json.getString(BINARY_NAME),
        json.getString(PATH),
        Optional.of(contractProperties));
  }

  private void executeContract(JsonObject json) {
    JsonObject contractArgument;
    if (json.containsKey(CONTRACT_ARGUMENT)) {
      contractArgument = createContractArgument(json.getJsonObject(CONTRACT_ARGUMENT));
    } else {
      contractArgument = Json.createObjectBuilder().build();
    }
    ContractExecutionResult result =
        util.executeContract(
            json.getString(ID), contractArgument, Optional.of(Json.createObjectBuilder().build()));

    if (result.getResult().isPresent()) {
      System.out.println(result.getResult().get().toString());
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

  private void checkAsset(JsonObject json) {
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

              if (!asset.containsKey(key)) {
                System.out.println("Key not found in asset. key:" + key);
                return;
              }
              JsonValue assetValue = asset.get(key);
              JsonValue value = jsonValueEntry.getValue();
              compare(value, assetValue);
            });
  }

  private void compare(JsonValue val1, JsonValue val2) {
    if (!(val1.getValueType() == val2.getValueType())) {
      System.out.println(
          "Json value type is not matched. " + val1.getValueType() + ":" + val2.getValueType());
      return;
    }

    switch (val1.getValueType()) {
      case STRING:
        if (!val1.toString().equals(val2.toString())) {
          System.out.println(
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
                    System.out.println("Key not found in asset. key:" + key);
                    return;
                  }
                  compare(jsonValueEntry.getValue(), obj2.get(key));
                });
        break;
    }
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
                  builder.add(key, clientProperties.getProperty(ClientConfig.CERT_HOLDER_ID));
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

    json.getJsonObject(OPTIONAL)
        .forEach(
            (key, value) -> {
              switch (value.getValueType()) {
                case STRING:
                  builder.add(key, ((JsonString) value).getString());
                  break;
                case OBJECT:
                  JsonObject object = value.asJsonObject();
                  switch (object.getString(TYPE)) {
                    case ClientConfig.CERT_HOLDER_ID:
                      builder.add(key, clientProperties.getProperty(ClientConfig.CERT_HOLDER_ID));
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

    return builder.build();
  }

  private void setContractProperties(JsonObject json) {
    clientProperties = new Properties();
    JsonObject jsonObject = json.getJsonObject(CLIENT_PROPERTIES);
    clientProperties.setProperty(
        ClientConfig.SERVER_HOST, jsonObject.getString(ClientConfig.SERVER_HOST, ""));
    clientProperties.setProperty(
        ClientConfig.SERVER_PORT, jsonObject.getString(ClientConfig.SERVER_PORT, ""));
    clientProperties.setProperty(
        ClientConfig.SERVER_PORT, jsonObject.getString(ClientConfig.SERVER_PORT, ""));
    clientProperties.setProperty(
        ClientConfig.SERVER_PRIVILEGED_PORT,
        jsonObject.getString(ClientConfig.SERVER_PRIVILEGED_PORT, ""));
    clientProperties.setProperty(
        ClientConfig.CLIENT_MODE, jsonObject.getString(ClientConfig.CLIENT_MODE, ""));
    clientProperties.setProperty(
        ClientConfig.CERT_HOLDER_ID, jsonObject.getString(ClientConfig.CERT_HOLDER_ID, ""));
    clientProperties.setProperty(
        ClientConfig.PRIVATE_KEY_PATH, jsonObject.getString(ClientConfig.PRIVATE_KEY_PATH, ""));
    clientProperties.setProperty(
        ClientConfig.PRIVATE_KEY_PEM, jsonObject.getString(ClientConfig.PRIVATE_KEY_PEM, ""));
    clientProperties.setProperty(
        ClientConfig.CERT_PATH, jsonObject.getString(ClientConfig.CERT_PATH, ""));
    clientProperties.setProperty(
        ClientConfig.CERT_PEM, jsonObject.getString(ClientConfig.CERT_PEM, ""));
    clientProperties.setProperty(
        ClientConfig.CERT_VERSION, jsonObject.getString(ClientConfig.CERT_VERSION, ""));
    clientProperties.setProperty(
        ClientConfig.TLS_ENABLED, jsonObject.getString(ClientConfig.TLS_ENABLED, ""));
    util.setup(clientProperties);
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
}
