package com.scalar.ist.function;

import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.COMPANY_METADATA;
import static com.scalar.ist.common.Constants.COMPANY_NAME;
import static com.scalar.ist.common.Constants.COMPANY_TABLE;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.RECORD_NOT_FOUND;
import static com.scalar.ist.common.Constants.UPDATED_AT;

import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.BooleanValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.dl.ledger.database.Database;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.function.Function;
import java.util.Optional;
import javax.json.JsonObject;

public class UpdateCompany extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    String companyId = contractArgument.getString(COMPANY_ID);
    String companyName = contractArgument.getString(COMPANY_NAME);
    JsonObject companyInformation = contractArgument.getJsonObject(COMPANY_METADATA);
    long createdAt = contractArgument.getJsonNumber(CREATED_AT).longValue();
    long updatedAt = contractArgument.getJsonNumber(UPDATED_AT).longValue();

    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey = new Key(new BigIntValue(CREATED_AT, createdAt));
    Get get = new Get(partitionKey, clusteringKey).forNamespace(NAMESPACE).forTable(COMPANY_TABLE);

    if (!database.get(get).isPresent()) {
      throw new ContractContextException(RECORD_NOT_FOUND);
    }

    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(new TextValue(COMPANY_NAME, companyName))
            .withValue(new TextValue(COMPANY_METADATA, companyInformation.toString()))
            .withValue(new BooleanValue(IS_ACTIVE, true))
            .withValue(new TextValue(CREATED_BY, contractProperties.get().getString(HOLDER_ID)))
            .withValue(new BigIntValue(UPDATED_AT, updatedAt))
            .forNamespace(NAMESPACE)
            .forTable(COMPANY_TABLE);
    if (contractArgument.containsKey(CORPORATE_NUMBER)) {
      put.withValue(new TextValue(CORPORATE_NUMBER, contractArgument.getString(CORPORATE_NUMBER)));
    }

    database.put(put);
  }
}
