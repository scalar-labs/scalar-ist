package com.scalar.ist.api.repository;

import static com.scalar.ist.api.model.DataSetSchema.CATEGORY_OF_DATA;
import static com.scalar.ist.api.model.DataSetSchema.CLASSIFICATION;
import static com.scalar.ist.api.model.DataSetSchema.COMPANY_ID;
import static com.scalar.ist.api.model.DataSetSchema.CREATED_AT;
import static com.scalar.ist.api.model.DataSetSchema.CREATED_BY;
import static com.scalar.ist.api.model.DataSetSchema.DATA_LOCATION;
import static com.scalar.ist.api.model.DataSetSchema.DATA_SET_NAME;
import static com.scalar.ist.api.model.DataSetSchema.DATA_SET_SCHEMA;
import static com.scalar.ist.api.model.DataSetSchema.DATA_TYPE;
import static com.scalar.ist.api.model.DataSetSchema.DESCRIPTION;
import static com.scalar.ist.api.model.DataSetSchema.IS_ACTIVE;
import static com.scalar.ist.api.model.DataSetSchema.ORGANIZATION_ID;
import static com.scalar.ist.api.model.DataSetSchema.UPDATED_AT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Result;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.BooleanValue;
import com.scalar.db.io.TextValue;
import com.scalar.ist.api.model.DataSetSchema;
import com.scalar.ist.api.model.DataSetSchema.DataLocation;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DataSetSchemasRepository extends ScalarDbReadOnlyRepository<DataSetSchema> {
  private final ObjectMapper objectMapper;

  @Autowired
  DataSetSchemasRepository(DistributedTransactionManager db, ObjectMapper objectMapper) {
    super(db);
    this.objectMapper = objectMapper;
  }

  @Override
  public DataSetSchema parse(@NotNull Result result) {
    DataSetSchema.DataSetSchemaBuilder builder = DataSetSchema.builder();
    ((TextValue) result.getValue(DATA_LOCATION).get())
        .getString()
        .ifPresent(
            (dataLocationString) -> {
              try {
                builder.dataLocation(
                    objectMapper.readValue(dataLocationString, DataLocation.class));
              } catch (JsonProcessingException e) {
                throw new RepositoryException(
                    "Error parsing the record for " + dataLocationString, e);
              }
            });
    ((TextValue) result.getValue(DATA_SET_SCHEMA).get())
        .getString()
        .ifPresent(
            (jsonSchemaString) -> {
              try {
                builder.dataSetSchema(objectMapper.readTree(jsonSchemaString));
              } catch (JsonProcessingException e) {
                throw new RepositoryException(
                    "Error parsing the record for " + jsonSchemaString, e);
              }
            });

    builder.createdAt(((BigIntValue) result.getValue(CREATED_AT).get()).get());
    ((TextValue) result.getValue(ORGANIZATION_ID).get())
        .getString()
        .ifPresent(builder::organizationId);
    ((TextValue) result.getValue(DATA_SET_NAME).get()).getString().ifPresent(builder::dataSetName);
    ((TextValue) result.getValue(DESCRIPTION).get()).getString().ifPresent(builder::description);
    ((TextValue) result.getValue(COMPANY_ID).get()).getString().ifPresent(builder::companyId);
    ((TextValue) result.getValue(CATEGORY_OF_DATA).get())
        .getString()
        .ifPresent(builder::categoryOfData);
    ((TextValue) result.getValue(DATA_TYPE).get()).getString().ifPresent(builder::dataType);
    ((TextValue) result.getValue(CLASSIFICATION).get())
        .getString()
        .ifPresent(builder::classification);
    builder.active(((BooleanValue) result.getValue(IS_ACTIVE).get()).get());
    ((TextValue) result.getValue(CREATED_BY).get()).getString().ifPresent(builder::createdBy);
    builder.updatedAt(((BigIntValue) result.getValue(UPDATED_AT).get()).get());
    return builder.build();
  }
}
