package com.scalar.ist.api.service;

import static com.scalar.ist.api.model.DataSetSchema.COMPANY_ID;
import static com.scalar.ist.api.model.DataSetSchema.CREATED_AT;
import static com.scalar.ist.api.model.DataSetSchema.ORGANIZATION_ID;
import static com.scalar.ist.api.model.DataSetSchema.TABLE_NAME;

import com.scalar.db.api.Get;
import com.scalar.db.api.Scan;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.ist.api.config.AppConfig;
import com.scalar.ist.api.controller.dto.ReadAllDataSetSchemasRequestBody;
import com.scalar.ist.api.model.DataSetSchema;
import com.scalar.ist.api.repository.ScalarDbReadOnlyRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSetSchemasService {
  private final ScalarDbReadOnlyRepository<DataSetSchema> repository;

  @Autowired
  public DataSetSchemasService(ScalarDbReadOnlyRepository<DataSetSchema> datasetSchemaRepository) {
    this.repository = datasetSchemaRepository;
  }

  public List<DataSetSchema> readAll(
      String companyId, @Nullable ReadAllDataSetSchemasRequestBody options) {
    List<DataSetSchema> dataSetSchemas = readAllCompanyDatasetSchema(companyId, options);
    if (options != null && options.isInactive()) {
      return dataSetSchemas;
    } else {
      return dataSetSchemas.stream().filter(DataSetSchema::isActive).collect(Collectors.toList());
    }
  }

  public List<DataSetSchema> readAllWithOrganizationId(
      String companyId, String organizationId, @Nullable ReadAllDataSetSchemasRequestBody options) {
    List<DataSetSchema> dataSetSchemas = readAllCompanyDatasetSchema(companyId, options);
    if (options != null && options.isInactive()) {
      return dataSetSchemas.stream()
          .filter(p -> p.getOrganizationId().equals(organizationId))
          .collect(Collectors.toList());
    } else {
      return dataSetSchemas.stream()
          .filter(p -> p.isActive() && p.getOrganizationId().equals(organizationId))
          .collect(Collectors.toList());
    }
  }

  public DataSetSchema read(String companyId, String organizationId, long createdAt) {
    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey =
        new Key(
            new BigIntValue(CREATED_AT, createdAt), new TextValue(ORGANIZATION_ID, organizationId));
    Get get =
        new Get(partitionKey, clusteringKey).forNamespace(AppConfig.NAMESPACE).forTable(TABLE_NAME);

    Optional<DataSetSchema> dataSetSchema = repository.get(get);
    dataSetSchema.orElseThrow(
        () ->
            new ObjectNotFoundException(
                DataSetSchema.class, companyId, Long.toString(createdAt), organizationId));
    return dataSetSchema.get();
  }

  private List<DataSetSchema> readAllCompanyDatasetSchema(
      String companyId, @Nullable ReadAllDataSetSchemasRequestBody options) {
    Scan scan =
        new Scan(new Key(new TextValue(COMPANY_ID, companyId)))
            .forNamespace(AppConfig.NAMESPACE)
            .forTable(TABLE_NAME)
            .withOrdering(new Scan.Ordering(CREATED_AT, Scan.Ordering.Order.DESC));
    if (options != null) {
      if (options.getStart() > 0) {
        scan.withStart(new Key(new BigIntValue(CREATED_AT, options.getStart())));
      }
      if (options.getEnd() > 0) {
        scan.withEnd(new Key(new BigIntValue(CREATED_AT, options.getEnd())));
      }
    }
    return repository.scan(scan);
  }
}
