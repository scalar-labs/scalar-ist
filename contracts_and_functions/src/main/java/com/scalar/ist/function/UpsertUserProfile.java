package com.scalar.ist.function;

import static com.scalar.ist.common.Constants.ASSET_IS_ALREADY_REGISTERED;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.UPDATED_AT;
import static com.scalar.ist.common.Constants.USER_PROFILE_TABLE;

import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.dl.ledger.database.Database;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.function.Function;
import java.util.Optional;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class UpsertUserProfile extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {

    Optional<Result> userProfile =
        get(
            database,
            contractArgument.getString(COMPANY_ID),
            contractArgument.getString(HOLDER_ID));
    if (contractArgument.getString(RECORD_MODE).equals(RECORD_MODE_INSERT)
        && userProfile.isPresent()) {
      throw new ContractContextException(ASSET_IS_ALREADY_REGISTERED);
    }

    String companyId = contractArgument.getString(COMPANY_ID);
    String newUserHolderId = contractArgument.getString(HOLDER_ID);
    JsonArray organizationIds = contractArgument.getJsonArray(ORGANIZATION_IDS);
    JsonArray roles = contractArgument.getJsonArray(ROLES);
    long createdAt = contractArgument.getJsonNumber(CREATED_AT).longValue();

    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey = new Key(new TextValue(HOLDER_ID, newUserHolderId));
    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(new TextValue(ORGANIZATION_IDS, organizationIds.toString()))
            .withValue(new TextValue(ROLES, roles.toString()))
            .withValue(new TextValue(CREATED_BY, contractProperties.get().getString(HOLDER_ID)))
            .withValue(new BigIntValue(UPDATED_AT, createdAt))
            .forNamespace(NAMESPACE)
            .forTable(USER_PROFILE_TABLE);
    if (contractArgument.getString(RECORD_MODE).equals(RECORD_MODE_INSERT)) {
      put.withValue(new BigIntValue(CREATED_AT, createdAt));
    }

    database.put(put);
  }

  private Optional<Result> get(Database database, String companyId, String holderId) {
    Get get =
        new Get(
                new Key(new TextValue(COMPANY_ID, companyId)),
                new Key(new TextValue(HOLDER_ID, holderId)))
            .forNamespace(NAMESPACE)
            .forTable(USER_PROFILE_TABLE);
    return database.get(get);
  }
}
