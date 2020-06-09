package com.scalar.ist.function;

import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.DATA_CATEGORY;
import static com.scalar.ist.common.Constants.DATA_CLASSIFICATION;
import static com.scalar.ist.common.Constants.DATA_LOCATION;
import static com.scalar.ist.common.Constants.DATA_SET_DESCRIPTION;
import static com.scalar.ist.common.Constants.DATA_SET_NAME;
import static com.scalar.ist.common.Constants.DATA_SET_SCHEMA;
import static com.scalar.ist.common.Constants.DATA_SET_SCHEMA_TABLE;
import static com.scalar.ist.common.Constants.DATA_TYPE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.UPDATED_AT;

import com.scalar.db.api.Put;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.BooleanValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.dl.ledger.database.Database;
import com.scalar.dl.ledger.function.Function;
import java.util.Optional;
import javax.json.JsonObject;

public class RegisterDataSetSchema extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    String companyId = contractArgument.getString(COMPANY_ID);
    long createdAt = contractArgument.getJsonNumber(CREATED_AT).longValue();
    String organizationId = contractArgument.getString(ORGANIZATION_ID);
    String dataSetName = contractArgument.getString(DATA_SET_NAME);
    String description = contractArgument.getString(DATA_SET_DESCRIPTION);
    String dataLocation = contractArgument.getJsonObject(DATA_LOCATION).toString();
    String dataCategory = contractArgument.getJsonArray(DATA_CATEGORY).toString();
    String dataType = contractArgument.getJsonArray(DATA_TYPE).toString();
    String classification = contractArgument.getJsonArray(DATA_CLASSIFICATION).toString();
    String dataSetSchema = contractArgument.getJsonObject(DATA_SET_SCHEMA).toString();
    String createdBy = contractProperties.get().getString(HOLDER_ID);

    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey =
        new Key(
            new BigIntValue(CREATED_AT, createdAt), new TextValue(ORGANIZATION_ID, organizationId));
    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(new TextValue(DATA_SET_NAME, dataSetName))
            .withValue(new TextValue(DATA_SET_DESCRIPTION, description))
            .withValue(new TextValue(DATA_LOCATION, dataLocation))
            .withValue(new TextValue(DATA_CATEGORY, dataCategory))
            .withValue(new TextValue(DATA_TYPE, dataType))
            .withValue(new TextValue(DATA_CLASSIFICATION, classification))
            .withValue(new TextValue(DATA_SET_SCHEMA, dataSetSchema))
            .withValue(new BooleanValue(IS_ACTIVE, true))
            .withValue(new TextValue(CREATED_BY, createdBy))
            .withValue(new BigIntValue(UPDATED_AT, createdAt))
            .forNamespace(NAMESPACE)
            .forTable(DATA_SET_SCHEMA_TABLE);

    database.put(put);
  }
}
