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
import static com.scalar.ist.common.Constants.RECORD_IS_ALREADY_REGISTERED;
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
import javax.json.JsonObject;

public class RegisterCompany extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {

    String companyId = contractArgument.getString(COMPANY_ID);

    long createdAt = contractArgument.getJsonNumber(CREATED_AT).longValue();
    if (get(database, companyId, createdAt).isPresent()) {
      throw new ContractContextException(RECORD_IS_ALREADY_REGISTERED);
    }

    String companyName = contractArgument.getString(COMPANY_NAME);
    String corporateNumber = contractArgument.getString(CORPORATE_NUMBER);
    JsonObject companyInformation = contractArgument.getJsonObject(COMPANY_METADATA);

    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey = new Key(new BigIntValue(CREATED_AT, createdAt));

    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(new TextValue(COMPANY_NAME, companyName))
            .withValue(new TextValue(CORPORATE_NUMBER, corporateNumber))
            .withValue(new TextValue(COMPANY_METADATA, companyInformation.toString()))
            .withValue(new BooleanValue(IS_ACTIVE, true))
            .withValue(new TextValue(CREATED_BY, contractProperties.get().getString(HOLDER_ID)))
            .withValue(new BigIntValue(UPDATED_AT, createdAt))
            .forNamespace(NAMESPACE)
            .forTable(COMPANY_TABLE);

    database.put(put);
  }

  private Optional<Result> get(Database database, String companyId, long createdAt) {
    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey = new Key(new BigIntValue(CREATED_AT, createdAt));
    Get get = new Get(partitionKey, clusteringKey).forNamespace(NAMESPACE).forTable(COMPANY_TABLE);
    return database.get(get);
  }
}
