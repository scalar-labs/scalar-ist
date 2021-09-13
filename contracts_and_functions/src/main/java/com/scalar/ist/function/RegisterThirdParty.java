package com.scalar.ist.function;

import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.THIRD_PARTY_DOMAIN;
import static com.scalar.ist.common.Constants.THIRD_PARTY_METADATA;
import static com.scalar.ist.common.Constants.THIRD_PARTY_NAME;
import static com.scalar.ist.common.Constants.THIRD_PARTY_TABLE;
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

public class RegisterThirdParty extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    String companyId = contractArgument.getString(COMPANY_ID);
    long createdAt = contractArgument.getJsonNumber(CREATED_AT).longValue();
    String organizationId = contractArgument.getString(THIRD_PARTY_DOMAIN);
    String thirdPartyName = contractArgument.getString(THIRD_PARTY_NAME);
    String thirdPartyInformation = contractArgument.getJsonObject(THIRD_PARTY_METADATA).toString();
    String organizations = contractArgument.getJsonArray(ORGANIZATIONS).toString();
    String createdBy = contractProperties.get().getString(HOLDER_ID);

    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey =
        new Key(
            new BigIntValue(CREATED_AT, createdAt),
            new TextValue(THIRD_PARTY_DOMAIN, organizationId));
    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(new TextValue(THIRD_PARTY_NAME, thirdPartyName))
            .withValue(new TextValue(THIRD_PARTY_METADATA, thirdPartyInformation))
            .withValue(new TextValue(ORGANIZATIONS, organizations))
            .withValue(new BooleanValue(IS_ACTIVE, true))
            .withValue(new TextValue(CREATED_BY, createdBy))
            .withValue(new BigIntValue(UPDATED_AT, createdAt))
            .forNamespace(NAMESPACE)
            .forTable(THIRD_PARTY_TABLE);
    if (contractArgument.containsKey(CORPORATE_NUMBER)) {
      put.withValue(new TextValue(CORPORATE_NUMBER, contractArgument.getString(CORPORATE_NUMBER)));
    }

    database.put(put);
  }
}
