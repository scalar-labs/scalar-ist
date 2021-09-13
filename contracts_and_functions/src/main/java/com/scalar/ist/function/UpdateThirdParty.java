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

import javax.json.JsonObject;
import java.util.Optional;

import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.RECORD_NOT_FOUND;
import static com.scalar.ist.common.Constants.THIRD_PARTY_DOMAIN;
import static com.scalar.ist.common.Constants.THIRD_PARTY_METADATA;
import static com.scalar.ist.common.Constants.THIRD_PARTY_NAME;
import static com.scalar.ist.common.Constants.THIRD_PARTY_TABLE;
import static com.scalar.ist.common.Constants.UPDATED_AT;

public class UpdateThirdParty extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    String companyId = contractArgument.getString(COMPANY_ID);
    long createdAt = contractArgument.getJsonNumber(CREATED_AT).longValue();
    String thirdPartyDomain = contractArgument.getString(THIRD_PARTY_DOMAIN);
    long updatedAt = contractArgument.getJsonNumber(UPDATED_AT).longValue();

    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey =
        new Key(
            new BigIntValue(CREATED_AT, createdAt),
            new TextValue(THIRD_PARTY_DOMAIN, thirdPartyDomain));
    get(database, partitionKey, clusteringKey)
        .orElseThrow(() -> new ContractContextException(RECORD_NOT_FOUND));

    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(
                new TextValue(THIRD_PARTY_NAME, contractArgument.getString(THIRD_PARTY_NAME)))
            .withValue(
                new TextValue(
                    THIRD_PARTY_METADATA,
                    contractArgument.getJsonObject(THIRD_PARTY_METADATA).toString()))
            .withValue(
                new TextValue(
                    ORGANIZATIONS, contractArgument.getJsonArray(ORGANIZATIONS).toString()))
            .withValue(new BooleanValue(IS_ACTIVE, true))
            .withValue(new TextValue(CREATED_BY, contractProperties.get().getString(HOLDER_ID)))
            .withValue(new BigIntValue(UPDATED_AT, updatedAt))
            .forNamespace(NAMESPACE)
            .forTable(THIRD_PARTY_TABLE);
    if (contractArgument.containsKey(CORPORATE_NUMBER)) {
      put.withValue(new TextValue(CORPORATE_NUMBER, contractArgument.getString(CORPORATE_NUMBER)));
    }

    database.put(put);
  }

  private Optional<Result> get(Database database, Key partitionKey, Key clusteringKey) {
    Get get =
        new Get(partitionKey, clusteringKey).forNamespace(NAMESPACE).forTable(THIRD_PARTY_TABLE);
    return database.get(get);
  }
}
