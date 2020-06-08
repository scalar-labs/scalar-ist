package com.scalar.ist.function;

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
import javax.json.JsonObject;

import static com.scalar.ist.common.Constants.*;

public class UpdateConsentStatementStatus extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {
    Result consentStatementResult = get(database, contractArgument);
    String consentStatementIdRoot =
        ((TextValue)
                consentStatementResult.getValue(CONSENT_STATEMENT_ROOT_CONSENT_STATEMENT_ID).get())
            .getString()
            .get();
    String version =
        ((TextValue) consentStatementResult.getValue(CONSENT_STATEMENT_VERSION).get())
            .getString()
            .get();
    String consentStatementId = contractArgument.getString(CONSENT_STATEMENT_ID);
    String companyId = contractArgument.getString(COMPANY_ID);
    String organizationId = contractArgument.getString(ORGANIZATION_ID);
    long updatedAt = contractArgument.getJsonNumber(UPDATED_AT).longValue();

    Key partitionKey =
        new Key(new TextValue(CONSENT_STATEMENT_ROOT_CONSENT_STATEMENT_ID, consentStatementIdRoot));
    Key clusteringKey =
        new Key(
            new TextValue(COMPANY_ID, companyId),
            new TextValue(ORGANIZATION_ID, organizationId),
            new TextValue(CONSENT_STATEMENT_ID, consentStatementId),
            new TextValue(CONSENT_STATEMENT_VERSION, version));

    Get get =
        new Get(partitionKey, clusteringKey)
            .forNamespace(NAMESPACE)
            .forTable(CONSENT_STATEMENT_TABLE);
    if (!database.get(get).isPresent()) {
      throw new ContractContextException(RECORD_NOT_FOUND);
    }

    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(
                new TextValue(
                    CONSENT_STATEMENT_STATUS, contractArgument.getString(CONSENT_STATEMENT_STATUS)))
            .withValue(new BigIntValue(UPDATED_AT, updatedAt))
            .forNamespace(NAMESPACE)
            .forTable(CONSENT_STATEMENT_TABLE);

    database.put(put);
  }

  private Result get(Database database, JsonObject contractArgument) {
    Get get =
        new Get(
                new Key(
                    new TextValue(
                        CONSENT_STATEMENT_ID, contractArgument.getString(CONSENT_STATEMENT_ID))))
            .forNamespace(NAMESPACE)
            .forTable(CONSENT_STATEMENT_TABLE);
    Optional<Result> optResult = database.get(get);
    if (optResult.isPresent()) {
      return optResult.get();
    } else {
      throw new ContractContextException("Consent Statement not found in the database");
    }
  }
}
