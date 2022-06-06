package com.scalar.ist.function;

import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.ORGANIZATION_ALREADY_IN_USE;
import static com.scalar.ist.common.Constants.ORGANIZATION_DESCRIPTION;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_METADATA;
import static com.scalar.ist.common.Constants.ORGANIZATION_NAME;
import static com.scalar.ist.common.Constants.ORGANIZATION_TABLE;
import static com.scalar.ist.common.Constants.UPDATED_AT;

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
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;

public class UpsertOrganization extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {

    String companyId = contractArgument.getString(COMPANY_ID);
    String organizationId = contractArgument.getString(ORGANIZATION_ID);
    long updatedAt = contractArgument.getJsonNumber(UPDATED_AT).longValue();

    Optional<Result> organization = get(database, organizationId);
    long createdAt = contractArgument.getJsonNumber(CREATED_AT).longValue();
    if (organization.isPresent()) {
      if (!((TextValue) organization.get().getValue(COMPANY_ID).get())
          .getString()
          .get()
          .equals(companyId)) {
        throw new ContractContextException(ORGANIZATION_ALREADY_IN_USE);
      }
      createdAt = ((BigIntValue) organization.get().getValue(CREATED_AT).get()).get();
    }

    String organizationMetadata =
        Json.createObjectBuilder()
            .add(ORGANIZATION_NAME, contractArgument.getString(ORGANIZATION_NAME))
            .add(ORGANIZATION_DESCRIPTION, contractArgument.getString(ORGANIZATION_DESCRIPTION))
            .build()
            .toString();
    String createdBy = contractProperties.get().getString(HOLDER_ID);

    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey =
        new Key(
            new TextValue(ORGANIZATION_ID, organizationId), new BigIntValue(CREATED_AT, createdAt));

    Get get =
        new Get(partitionKey, clusteringKey).forNamespace(NAMESPACE).forTable(ORGANIZATION_TABLE);
    database.get(get);

    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(new TextValue(ORGANIZATION_METADATA, organizationMetadata))
            .withValue(new BooleanValue(IS_ACTIVE, contractArgument.getBoolean(IS_ACTIVE)))
            .withValue(new TextValue(CREATED_BY, createdBy))
            .withValue(new BigIntValue(UPDATED_AT, updatedAt))
            .forNamespace(NAMESPACE)
            .forTable(ORGANIZATION_TABLE);

    database.put(put);
  }

  private Optional<Result> get(Database database, String organizationId) {
    Get get =
        new Get(new Key(new TextValue(ORGANIZATION_ID, organizationId)))
            .forNamespace(NAMESPACE)
            .forTable(ORGANIZATION_TABLE);
    return database.get(get);
  }
}
