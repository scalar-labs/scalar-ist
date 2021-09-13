package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.DISALLOWED_CONTRACT_EXECUTION_ORDER;
import static com.scalar.ist.common.Constants.INVALID_CONTRACT_ARGUMENTS;
import static com.scalar.ist.common.Constants.INVALID_CONTRACT_ARGUMENTS_SCHEMA;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;

import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import java.util.Optional;
import javax.json.JsonObject;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Schema validator for contract arguments. This contract validates javax.JsonObject using a given
 * JSON schema. The JsonObject being validated is passed in the `contract_argument` field, and the
 * schema used to validate the JsonObject is passed in the `schema` field in the argument of the
 * invoke function.
 */
public class ValidateArgument extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(argument);
    return null;
  }

  private void validate(JsonObject argument) {
    validateExecutionOrder();

    Schema schema;
    JSONObject argumentsNode;

    try {
      schema =
          SchemaLoader.builder()
              .draftV7Support()
              .build()
              .load(
                  new JSONObject(
                      new JSONTokener(
                          argument.getJsonObject(VALIDATE_ARGUMENT_SCHEMA).toString())));
      argumentsNode =
          new JSONObject(
              new JSONTokener(
                  argument.getJsonObject(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT).toString()));
    } catch (Exception e) {
      throw new ContractContextException(INVALID_CONTRACT_ARGUMENTS_SCHEMA, e);
    }
    try {
      schema.validate(argumentsNode);
    } catch (ValidationException e) {
      String errorsMessage = String.join(", ", e.getAllMessages());
      throw new ContractContextException(
          String.format("%s [%s]", INVALID_CONTRACT_ARGUMENTS, errorsMessage));
    }
  }

  private void validateExecutionOrder() {
    if (isRoot()) {
      throw new ContractContextException(DISALLOWED_CONTRACT_EXECUTION_ORDER);
    }
  }
}
