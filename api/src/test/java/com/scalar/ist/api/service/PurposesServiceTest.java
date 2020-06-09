package com.scalar.ist.api.service;

import static com.scalar.ist.api.model.Purpose.COMPANY_ID;
import static com.scalar.ist.api.model.Purpose.CREATED_AT;
import static com.scalar.ist.api.model.Purpose.ORGANIZATION_ID;
import static com.scalar.ist.api.model.Purpose.TABLE_NAME;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Scan;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.ist.api.config.AppConfig;
import com.scalar.ist.api.controller.dto.ReadAllPurposesRequestBody;
import com.scalar.ist.api.model.Purpose;
import com.scalar.ist.api.repository.ScalarDbReadOnlyRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PurposesServiceTest {
  private static final String MOCK_COMPANY_ID = "scalar-labs";
  @Mock DistributedTransactionManager distributedTransactionManager;
  @Mock ScalarDbReadOnlyRepository<Purpose> repository;
  PurposesService service;

  @Before
  public void setup() {
    service = spy(new PurposesService(repository));
  }

  @Test
  public void readAll_WithoutOptions_ShouldReturnOnlyActivePurpose() {
    // Arrange
    Scan scan =
        new Scan(new Key(new TextValue(COMPANY_ID, MOCK_COMPANY_ID)))
            .forNamespace(AppConfig.NAMESPACE)
            .forTable(TABLE_NAME)
            .withOrdering(new Scan.Ordering(CREATED_AT, Scan.Ordering.Order.DESC));
    List<Purpose> purposes = preparePurposes(MOCK_COMPANY_ID, UUID.randomUUID().toString());
    when(repository.scan(scan)).thenReturn(purposes);

    // Act
    List<Purpose> result = service.readAll(MOCK_COMPANY_ID, null);

    // Assert
    verify(repository).scan(scan);
    Assertions.assertThat(result)
        .isEqualTo(purposes.stream().filter(Purpose::isActive).collect(Collectors.toList()));
  }

  @Test
  public void readAll_WithOptions_ShouldReturnAllPurpose() {
    // Arrange
    int start = 1;
    int end = 2;
    Scan scan =
        new Scan(new Key(new TextValue(COMPANY_ID, MOCK_COMPANY_ID)))
            .forNamespace(AppConfig.NAMESPACE)
            .withStart(new Key(new BigIntValue(CREATED_AT, start)))
            .withEnd(new Key(new BigIntValue(CREATED_AT, end)))
            .forTable(TABLE_NAME)
            .withOrdering(new Scan.Ordering(CREATED_AT, Scan.Ordering.Order.DESC));
    List<Purpose> purposes = preparePurposes(MOCK_COMPANY_ID, UUID.randomUUID().toString());
    when(repository.scan(scan)).thenReturn(purposes);
    ReadAllPurposesRequestBody options =
        ReadAllPurposesRequestBody.builder().start(start).end(end).inactive(true).build();

    // Act
    List<Purpose> result = service.readAll(MOCK_COMPANY_ID, options);

    // Assert
    verify(repository).scan(scan);
    Assertions.assertThat(result).isEqualTo(purposes);
  }

  @Test
  public void readAllWithOrganizationId_WithoutOptions_ShouldReturnOnlyActivePurpose() {
    // Arrange
    String organizationId = UUID.randomUUID().toString();
    Scan scan =
        new Scan(new Key(new TextValue(COMPANY_ID, MOCK_COMPANY_ID)))
            .forNamespace(AppConfig.NAMESPACE)
            .forTable(TABLE_NAME)
            .withOrdering(new Scan.Ordering(CREATED_AT, Scan.Ordering.Order.DESC));
    List<Purpose> org1Purposes = preparePurposes(MOCK_COMPANY_ID, "org1");
    List<Purpose> org2Purposes = preparePurposes(MOCK_COMPANY_ID, "org2");
    List<Purpose> allPurposes = new ArrayList<>(org1Purposes);
    allPurposes.addAll(org2Purposes);
    when(repository.scan(scan)).thenReturn(allPurposes);

    // Act
    List<Purpose> result = service.readAllWithOrganizationId(MOCK_COMPANY_ID, "org1", null);

    // Assert
    verify(repository).scan(scan);
    Assertions.assertThat(result)
        .isEqualTo(org1Purposes.stream().filter(Purpose::isActive).collect(Collectors.toList()));
  }

  @Test
  public void readAllWithOrganizationId_WithOptions_ShouldReturnOnlyActivePurpose() {
    // Arrange
    int start = 1;
    int end = 2;
    ReadAllPurposesRequestBody options =
        ReadAllPurposesRequestBody.builder().start(start).end(end).inactive(true).build();
    Scan scan =
        new Scan(new Key(new TextValue(COMPANY_ID, MOCK_COMPANY_ID)))
            .forNamespace(AppConfig.NAMESPACE)
            .forTable(TABLE_NAME)
            .withStart(new Key(new BigIntValue(CREATED_AT, start)))
            .withEnd(new Key(new BigIntValue(CREATED_AT, end)))
            .withOrdering(new Scan.Ordering(CREATED_AT, Scan.Ordering.Order.DESC));
    List<Purpose> org1Purposes = preparePurposes(MOCK_COMPANY_ID, "org1");
    List<Purpose> org2Purposes = preparePurposes(MOCK_COMPANY_ID, "org2");
    List<Purpose> allPurposes = new ArrayList<>(org1Purposes);
    allPurposes.addAll(org2Purposes);
    when(repository.scan(scan)).thenReturn(allPurposes);

    // Act
    List<Purpose> result = service.readAllWithOrganizationId(MOCK_COMPANY_ID, "org2", options);

    // Assert
    verify(repository).scan(scan);
    Assertions.assertThat(result).isEqualTo(org2Purposes);
  }

  @Test
  public void read_WithCorrectArguments_ShouldReturnOnlyActivePurpose() {
    // Arrange
    Purpose purpose =
        Purpose.builder()
            .companyId(MOCK_COMPANY_ID)
            .createdAt(2)
            .organizationId(UUID.randomUUID().toString())
            .categoryOfPurpose("marketing/customer_service")
            .purposeName("marketingGoal")
            .description("bar")
            .legalText("lorem ipsum")
            .userFriendlyText("foo")
            .guidance("zoo")
            .note("none")
            .active(true)
            .createdBy(UUID.randomUUID().toString())
            .build();
    Get get =
        new Get(
                new Key(new TextValue(COMPANY_ID, purpose.getCompanyId())),
                new Key(
                    new BigIntValue(CREATED_AT, purpose.getCreatedAt()),
                    new TextValue(ORGANIZATION_ID, purpose.getOrganizationId())))
            .forNamespace(AppConfig.NAMESPACE)
            .forTable(TABLE_NAME);
    when(repository.get(get)).thenReturn(Optional.of(purpose));

    // Act
    Purpose result =
        service.read(purpose.getCompanyId(), purpose.getOrganizationId(), purpose.getCreatedAt());

    // Assert
    verify(repository).get(get);
    Assertions.assertThat(result).isEqualTo(purpose);
  }

  private List<Purpose> preparePurposes(String companyId, String organizationId) {
    return Arrays.asList(
        Purpose.builder()
            .companyId(companyId)
            .createdAt(2)
            .organizationId(organizationId)
            .categoryOfPurpose("marketing/customer_service")
            .purposeName("marketingGoal")
            .description("bar")
            .legalText("lorem ipsum")
            .userFriendlyText("foo")
            .guidance("zoo")
            .note("none")
            .active(true)
            .createdBy(UUID.randomUUID().toString())
            .updatedAt(3)
            .build(),
        Purpose.builder()
            .companyId(companyId)
            .createdAt(1)
            .organizationId(organizationId)
            .categoryOfPurpose("marketing/customer_service")
            .purposeName("marketingGoal_2")
            .description("bar")
            .legalText("lorem ipsum")
            .userFriendlyText("foo")
            .guidance("zoo")
            .note("none")
            .active(false)
            .createdBy(UUID.randomUUID().toString())
            .updatedAt(3)
            .build());
  }
}
