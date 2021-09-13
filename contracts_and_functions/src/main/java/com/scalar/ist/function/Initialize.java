package com.scalar.ist.function;

import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_SYSADMIN;
import static com.scalar.ist.common.Constants.HOLDER_ID_SYSOPERATOR;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.REGISTER_COMPANY;
import static com.scalar.ist.common.Constants.REGISTER_ORGANIZATION;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLE_SYSADMIN;
import static com.scalar.ist.common.Constants.ROLE_SYSOPERATOR;
import static com.scalar.ist.common.Constants.UPSERT_USER_PROFILE;

import com.scalar.dl.ledger.database.Database;
import com.scalar.dl.ledger.function.Function;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;

public class Initialize extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {

    JsonObject sysAdminArgument =
        createUserProfileArgument(contractArgument, ROLE_SYSADMIN, HOLDER_ID_SYSADMIN);
    JsonObject opAdminArgument =
        createUserProfileArgument(contractArgument, ROLE_SYSOPERATOR, HOLDER_ID_SYSOPERATOR);

    invoke(REGISTER_COMPANY, database, functionArgument, contractArgument, contractProperties);
    invoke(REGISTER_ORGANIZATION, database, functionArgument, contractArgument, contractProperties);
    invoke(UPSERT_USER_PROFILE, database, functionArgument, opAdminArgument, contractProperties);
    invoke(UPSERT_USER_PROFILE, database, functionArgument, sysAdminArgument, contractProperties);
  }

  private JsonObject createUserProfileArgument(
      JsonObject contractArgument, String role, String holder_id) {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, contractArgument.getString(COMPANY_ID))
        .add(HOLDER_ID, contractArgument.getString(holder_id))
        .add(
            ORGANIZATION_IDS,
            Json.createArrayBuilder().add(contractArgument.getString(ORGANIZATION_ID)).build())
        .add(ROLES, Json.createArrayBuilder().add(role).build())
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(CREATED_AT, contractArgument.getJsonNumber(CREATED_AT))
        .build();
  }
}
