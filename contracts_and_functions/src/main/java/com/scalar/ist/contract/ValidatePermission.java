package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
import static com.scalar.ist.common.Constants.DISALLOWED_CONTRACT_EXECUTION_ORDER;
import static com.scalar.ist.common.Constants.PERMISSION_DENIED;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.USER_PROFILE_ROLES;

import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import java.util.Optional;
import javax.json.JsonObject;

public class ValidatePermission extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    validate(argument);

    return null;
  }

  private void validate(JsonObject argument) {
    validateExecutionOrder();

    if (argument.getJsonArray(ROLES_REQUIRED).stream()
        .noneMatch(argument.getJsonArray(USER_PROFILE_ROLES)::contains)) {
      throw new ContractContextException(PERMISSION_DENIED);
    }

    if (!argument.containsKey(ORGANIZATION_IDS_REQUIRED)
        && !argument.containsKey(ORGANIZATION_IDS_ARGUMENT)) {
      return;
    }
    if (!argument
        .getJsonArray(ORGANIZATION_IDS_REQUIRED)
        .containsAll(argument.getJsonArray(ORGANIZATION_IDS_ARGUMENT))) {
      throw new ContractContextException(PERMISSION_DENIED);
    }
  }

  private void validateExecutionOrder() {
    if (isRoot()) {
      throw new ContractContextException(DISALLOWED_CONTRACT_EXECUTION_ORDER);
    }
  }
}
