package com.scalar.ist.tools;

import com.scalar.dl.client.config.ClientConfig;

import javax.json.*;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

import static com.scalar.ist.tools.Constants.*;

public class Deploy {
  private ContractUtil util;
  private Properties clientProperties;

  public void process(JsonArray array) {

    util = new ContractUtil();
    array.forEach(
        jsonValue -> {
          JsonObject json = (JsonObject) jsonValue;
          switch (json.getString(ACTION)) {
            case SET_HOLDER:
              setHolder(json);
              return;
            case REGISTER_CERT:
              registerCert();
              return;
            case REGISTER_FUNCTIONS:
              registerFunctions(json);
              return;
            case REGISTER_CONTRACT:
              registerContract(json);
              return;
            case EXECUTE_CONTRACT:
              executeContract(json);
          }
        });
  }

  private void setHolder(JsonObject json) {
    System.out.println(Json.createObjectBuilder().add(ACTION, SET_HOLDER).build());

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

  private void registerCert() {
    System.out.println(Json.createObjectBuilder().add(ACTION, REGISTER_CERT).build());
    util.registerCertificate();
  }

  private void registerFunctions(JsonObject json) {
    System.out.println(Json.createObjectBuilder().add(ACTION, REGISTER_FUNCTIONS).build());
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
    System.out.println(Json.createObjectBuilder().add(ACTION, REGISTER_CONTRACT).build());

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
    System.out.println(Json.createObjectBuilder().add(ACTION, EXECUTE_CONTRACT).build());

    JsonObject contractArgument;
    if (json.containsKey(CONTRACT_ARGUMENT)) {
      contractArgument = createContractArgument(json.getJsonObject(CONTRACT_ARGUMENT));
    } else {
      contractArgument = Json.createObjectBuilder().build();
    }
    System.out.println(contractArgument);

    util.executeContract(
        json.getString(ID), contractArgument, Optional.of(Json.createObjectBuilder().build()));
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

  private JsonObject createContractArgument(JsonObject json) {

    JsonObject argument = null;
    JsonObject argumentValue = json.getJsonObject(VALUE);
    switch (argumentValue.getString(TYPE)) {
      case TYPE_STRING:
        argument = argumentValue.getJsonObject(VALUE);
        break;

      case TYPE_FILE:
        argument = Util.readJsonFromFile(argumentValue.getString(PATH));
        break;
    }
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
}
