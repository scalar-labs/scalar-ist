package com.scalar.ist.function;

import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.RETENTION_POLICY_DESCRIPTION;
import static com.scalar.ist.common.Constants.RETENTION_POLICY_NAME;
import static com.scalar.ist.common.Constants.RETENTION_POLICY_RETENTION_DURATION;
import static com.scalar.ist.common.Constants.RETENTION_POLICY_TABLE;
import static com.scalar.ist.common.Constants.RETENTION_POLICY_TYPE;
import static com.scalar.ist.common.Constants.RETENTION_POLICY_USE_DURATION;
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

public class RegisterDataRetentionPolicy extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    String companyId = contractArgument.getString(COMPANY_ID);
    long createdAt = contractArgument.getJsonNumber(CREATED_AT).longValue();
    String policyType = contractArgument.getString(RETENTION_POLICY_TYPE);
    String organizationId = contractArgument.getString(ORGANIZATION_ID);
    String policyName = contractArgument.getString(RETENTION_POLICY_NAME);
    String lengthOfUse = contractArgument.getString(RETENTION_POLICY_USE_DURATION);
    String lengthOfRetention = contractArgument.getString(RETENTION_POLICY_RETENTION_DURATION);
    String description = contractArgument.getString(RETENTION_POLICY_DESCRIPTION);
    String createdBy = contractProperties.get().getString(HOLDER_ID);

    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey =
        new Key(
            new BigIntValue(CREATED_AT, createdAt),
            new TextValue(RETENTION_POLICY_TYPE, policyType),
            new TextValue(ORGANIZATION_ID, organizationId));
    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(new TextValue(RETENTION_POLICY_NAME, policyName))
            .withValue(new TextValue(RETENTION_POLICY_USE_DURATION, lengthOfUse))
            .withValue(new TextValue(RETENTION_POLICY_RETENTION_DURATION, lengthOfRetention))
            .withValue(new TextValue(RETENTION_POLICY_DESCRIPTION, description))
            .withValue(new BooleanValue(IS_ACTIVE, true))
            .withValue(new TextValue(CREATED_BY, createdBy))
            .withValue(new BigIntValue(UPDATED_AT, createdAt))
            .forNamespace(NAMESPACE)
            .forTable(RETENTION_POLICY_TABLE);

    database.put(put);
  }
}
