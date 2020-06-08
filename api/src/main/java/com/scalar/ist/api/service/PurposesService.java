package com.scalar.ist.api.service;

import static com.scalar.ist.api.model.Purpose.COMPANY_ID;
import static com.scalar.ist.api.model.Purpose.CREATED_AT;
import static com.scalar.ist.api.model.Purpose.ORGANIZATION_ID;
import static com.scalar.ist.api.model.Purpose.TABLE_NAME;

import com.scalar.db.api.Get;
import com.scalar.db.api.Scan;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.ist.api.config.AppConfig;
import com.scalar.ist.api.controller.dto.ReadAllPurposesRequestBody;
import com.scalar.ist.api.model.Purpose;
import com.scalar.ist.api.repository.ScalarDbReadOnlyRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurposesService {
  private final ScalarDbReadOnlyRepository<Purpose> repository;

  @Autowired
  public PurposesService(ScalarDbReadOnlyRepository<Purpose> purposeRepository) {
    this.repository = purposeRepository;
  }

  public List<Purpose> readAll(String companyId, @Nullable ReadAllPurposesRequestBody options) {
    List<Purpose> purposes = readAllCompanyPurposes(companyId, options);
    if (options != null && options.isInactive()) {
      return purposes;
    } else {
      return purposes.stream().filter(Purpose::isActive).collect(Collectors.toList());
    }
  }

  public List<Purpose> readAllWithOrganizationId(
      String companyId, String organizationId, @Nullable ReadAllPurposesRequestBody options) {
    List<Purpose> purposes = readAllCompanyPurposes(companyId, options);
    if (options != null && options.isInactive()) {
      return purposes.stream()
          .filter(p -> p.getOrganizationId().equals(organizationId))
          .collect(Collectors.toList());
    } else {
      return purposes.stream()
          .filter(p -> p.isActive() && p.getOrganizationId().equals(organizationId))
          .collect(Collectors.toList());
    }
  }

  public Purpose read(String companyId, String organizationId, long createdAt) {
    Key partitionKey = new Key(new TextValue(COMPANY_ID, companyId));
    Key clusteringKey =
        new Key(
            new BigIntValue(CREATED_AT, createdAt), new TextValue(ORGANIZATION_ID, organizationId));
    Get get =
        new Get(partitionKey, clusteringKey).forNamespace(AppConfig.NAMESPACE).forTable(TABLE_NAME);
    return repository
        .get(get)
        .orElseThrow(
            () ->
                new ObjectNotFoundException(
                    Purpose.class, companyId, Long.toString(createdAt), organizationId));
  }

  private List<Purpose> readAllCompanyPurposes(
      String companyId, @Nullable ReadAllPurposesRequestBody options) {
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
