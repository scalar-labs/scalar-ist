package com.scalar.ist.function;

import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.BooleanValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.dl.ledger.database.Database;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.function.Function;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Optional;

import static com.scalar.ist.common.Constants.ADMIN;
import static com.scalar.ist.common.Constants.ADMINISTRATOR_ORGANIZATION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.ORGANIZATION_DESCRIPTION;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_METADATA;
import static com.scalar.ist.common.Constants.ORGANIZATION_NAME;
import static com.scalar.ist.common.Constants.ORGANIZATION_TABLE;
import static com.scalar.ist.common.Constants.RECORD_IS_ALREADY_REGISTERED;

public class RegisterOrganization extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {

    String companyId = contractArgument.getString(COMPANY_ID);
    String organizationId = contractArgument.getString(ORGANIZATION_ID);
    long createdAt = contractArgument.getJsonNumber(CREATED_AT).longValue();
    if (get(database, companyId, organizationId, createdAt).isPresent()) {
      throw new ContractContextException(RECORD_IS_ALREADY_REGISTERED);
    }

    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey =
        new Key(
            new TextValue(ORGANIZATION_ID, organizationId), new BigIntValue(CREATED_AT, createdAt));

    JsonObject organizationInformation =
        Json.createObjectBuilder()
            .add(ORGANIZATION_NAME, ADMIN)
            .add(ORGANIZATION_DESCRIPTION, ADMINISTRATOR_ORGANIZATION)
            .build();
    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(new TextValue(ORGANIZATION_METADATA, organizationInformation.toString()))
            .withValue(new BooleanValue(IS_ACTIVE, true))
            .withValue(new TextValue(CREATED_BY, contractProperties.get().getString(HOLDER_ID)))
            .forNamespace(NAMESPACE)
            .forTable(ORGANIZATION_TABLE);

    database.put(put);
  }

  private Optional<Result> get(
      Database database, String companyId, String organizationId, long createdAt) {
    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey =
        new Key(
            new TextValue(ORGANIZATION_ID, organizationId), new BigIntValue(CREATED_AT, createdAt));
    Get get =
        new Get(partitionKey, clusteringKey).forNamespace(NAMESPACE).forTable(ORGANIZATION_TABLE);
    return database.get(get);
  }
}
