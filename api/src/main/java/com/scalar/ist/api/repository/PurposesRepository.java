package com.scalar.ist.api.repository;

import static com.scalar.ist.api.model.Purpose.CATEGORY_OF_PURPOSE;
import static com.scalar.ist.api.model.Purpose.COMPANY_ID;
import static com.scalar.ist.api.model.Purpose.CREATED_AT;
import static com.scalar.ist.api.model.Purpose.CREATED_BY;
import static com.scalar.ist.api.model.Purpose.DESCRIPTION;
import static com.scalar.ist.api.model.Purpose.GUIDANCE;
import static com.scalar.ist.api.model.Purpose.IS_ACTIVE;
import static com.scalar.ist.api.model.Purpose.LEGAL_TEXT;
import static com.scalar.ist.api.model.Purpose.NOTE;
import static com.scalar.ist.api.model.Purpose.ORGANIZATION_ID;
import static com.scalar.ist.api.model.Purpose.PURPOSE_NAME;
import static com.scalar.ist.api.model.Purpose.UPDATED_AT;
import static com.scalar.ist.api.model.Purpose.USER_FRIENDLY_TEXT;

import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Result;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.BooleanValue;
import com.scalar.db.io.TextValue;
import com.scalar.ist.api.model.Purpose;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PurposesRepository extends ScalarDbReadOnlyRepository<Purpose> {
  @Autowired
  PurposesRepository(DistributedTransactionManager db) {
    super(db);
  }

  @Override
  public Purpose parse(@NotNull Result result) {
    Purpose.PurposeBuilder builder = Purpose.builder();
    ((TextValue) result.getValue(COMPANY_ID).get()).getString().ifPresent(builder::companyId);
    builder.createdAt(((BigIntValue) result.getValue(CREATED_AT).get()).get());
    ((TextValue) result.getValue(ORGANIZATION_ID).get())
        .getString()
        .ifPresent(builder::organizationId);
    ((TextValue) result.getValue(CATEGORY_OF_PURPOSE).get())
        .getString()
        .ifPresent(builder::categoryOfPurpose);
    ((TextValue) result.getValue(PURPOSE_NAME).get()).getString().ifPresent(builder::purposeName);
    ((TextValue) result.getValue(DESCRIPTION).get()).getString().ifPresent(builder::description);
    ((TextValue) result.getValue(LEGAL_TEXT).get()).getString().ifPresent(builder::legalText);
    ((TextValue) result.getValue(USER_FRIENDLY_TEXT).get())
        .getString()
        .ifPresent(builder::userFriendlyText);
    ((TextValue) result.getValue(GUIDANCE).get()).getString().ifPresent(builder::guidance);
    ((TextValue) result.getValue(NOTE).get()).getString().ifPresent(builder::note);
    builder.active(((BooleanValue) result.getValue(IS_ACTIVE).get()).get());
    ((TextValue) result.getValue(CREATED_BY).get()).getString().ifPresent(builder::createdBy);
    builder.updatedAt(((BigIntValue) result.getValue(UPDATED_AT).get()).get());
    return builder.build();
  }
}
