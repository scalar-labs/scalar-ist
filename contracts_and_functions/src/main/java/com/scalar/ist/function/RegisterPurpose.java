package com.scalar.ist.function;

import com.scalar.db.api.Put;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.BooleanValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.dl.ledger.database.Database;
import com.scalar.dl.ledger.function.Function;

import javax.json.JsonObject;
import java.util.Optional;

import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.CREATED_BY;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.NAMESPACE;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.PURPOSE_CATEGORY_OF_PURPOSE;
import static com.scalar.ist.common.Constants.PURPOSE_DESCRIPTION;
import static com.scalar.ist.common.Constants.PURPOSE_GUIDANCE;
import static com.scalar.ist.common.Constants.PURPOSE_LEGAL_TEXT;
import static com.scalar.ist.common.Constants.PURPOSE_NAME;
import static com.scalar.ist.common.Constants.PURPOSE_NOTE;
import static com.scalar.ist.common.Constants.PURPOSE_TABLE;
import static com.scalar.ist.common.Constants.PURPOSE_USER_FRIENDLY_TEXT;
import static com.scalar.ist.common.Constants.UPDATED_AT;

public class RegisterPurpose extends Function {

  @Override
  public void invoke(
      Database database,
      Optional<JsonObject> functionArgument,
      JsonObject contractArgument,
      Optional<JsonObject> contractProperties) {

    String companyId = contractArgument.getString(COMPANY_ID);
    String holderId = contractProperties.get().getString(HOLDER_ID);
    String organizationId = contractArgument.getString(ORGANIZATION_ID);
    String categoryOfPurpose = contractArgument.getString(PURPOSE_CATEGORY_OF_PURPOSE);
    String purposeName = contractArgument.getString(PURPOSE_NAME);
    String description = contractArgument.getString(PURPOSE_DESCRIPTION);
    String legalText = contractArgument.getString(PURPOSE_LEGAL_TEXT);
    String userFriendlyText = contractArgument.getString(PURPOSE_USER_FRIENDLY_TEXT);
    String guidance = contractArgument.getString(PURPOSE_GUIDANCE);
    String note = contractArgument.getString(PURPOSE_NOTE);
    long createdAt = contractArgument.getJsonNumber(CREATED_AT).longValue();

    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey =
        new Key(
            new BigIntValue(CREATED_AT, createdAt), new TextValue(ORGANIZATION_ID, organizationId));

    Put put =
        new Put(partitionKey, clusteringKey)
            .withValue(new TextValue(PURPOSE_CATEGORY_OF_PURPOSE, categoryOfPurpose))
            .withValue(new TextValue(PURPOSE_NAME, purposeName))
            .withValue(new TextValue(PURPOSE_DESCRIPTION, description))
            .withValue(new TextValue(PURPOSE_LEGAL_TEXT, legalText))
            .withValue(new TextValue(PURPOSE_USER_FRIENDLY_TEXT, userFriendlyText))
            .withValue(new TextValue(PURPOSE_GUIDANCE, guidance))
            .withValue(new TextValue(PURPOSE_NOTE, note))
            .withValue(new TextValue(CREATED_BY, holderId))
            .withValue(new BooleanValue(IS_ACTIVE, true))
            .withValue(new BigIntValue(UPDATED_AT, createdAt))
            .forNamespace(NAMESPACE)
            .forTable(PURPOSE_TABLE);

    database.put(put);
  }
}
