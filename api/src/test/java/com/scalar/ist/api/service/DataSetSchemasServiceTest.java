package com.scalar.ist.api.service;

import static com.scalar.ist.api.model.DataSetSchema.ORGANIZATION_ID;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Scan;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.ist.api.config.AppConfig;
import com.scalar.ist.api.controller.dto.ReadAllDataSetSchemasRequestBody;
import com.scalar.ist.api.model.DataSetSchema;
import com.scalar.ist.api.model.DataSetSchema.DataLocation;
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
public class DataSetSchemasServiceTest {
  private static final String MOCK_COMPANY_ID = "scalar-labs";
  @Mock ScalarDbReadOnlyRepository<DataSetSchema> repository;
  DataSetSchemasService service;

  @Before
  public void setup() {
    service = spy(new DataSetSchemasService(repository));
  }

  @Test
  public void readAll_WithoutOptions_ShouldReturnOnlyActiveDatasetSchema() {
    // Arrange
    Scan scan =
        new Scan(new Key(new TextValue(DataSetSchema.COMPANY_ID, MOCK_COMPANY_ID)))
            .forNamespace(AppConfig.NAMESPACE)
            .forTable(DataSetSchema.TABLE_NAME)
            .withOrdering(new Scan.Ordering(DataSetSchema.CREATED_AT, Scan.Ordering.Order.DESC));
    List<DataSetSchema> dataSetSchemas = prepareDatasetSchemas(UUID.randomUUID().toString());
    when(repository.scan(scan)).thenReturn(dataSetSchemas);

    // Act
    List<DataSetSchema> result = service.readAll(MOCK_COMPANY_ID, null);

    // Assert
    verify(repository).scan(scan);
    Assertions.assertThat(result)
        .isEqualTo(
            dataSetSchemas.stream().filter(DataSetSchema::isActive).collect(Collectors.toList()));
  }

  @Test
  public void readAll_WithOptions_ShouldReturnAllDatasetSchema() {
    // Arrange
    int start = 1;
    int end = 2;
    Scan scan =
        new Scan(new Key(new TextValue(DataSetSchema.COMPANY_ID, MOCK_COMPANY_ID)))
            .forNamespace(AppConfig.NAMESPACE)
            .withStart(new Key(new BigIntValue(DataSetSchema.CREATED_AT, start)))
            .withEnd(new Key(new BigIntValue(DataSetSchema.CREATED_AT, end)))
            .forTable(DataSetSchema.TABLE_NAME)
            .withOrdering(new Scan.Ordering(DataSetSchema.CREATED_AT, Scan.Ordering.Order.DESC));
    List<DataSetSchema> dataSetSchemas = prepareDatasetSchemas(UUID.randomUUID().toString());
    when(repository.scan(scan)).thenReturn(dataSetSchemas);
    ReadAllDataSetSchemasRequestBody options =
        ReadAllDataSetSchemasRequestBody.builder().start(start).end(end).inactive(true).build();

    // Act
    List<DataSetSchema> result = service.readAll(MOCK_COMPANY_ID, options);

    // Assert
    verify(repository).scan(scan);
    Assertions.assertThat(result).isEqualTo(dataSetSchemas);
  }

  @Test
  public void readAllWithOrganizationId_WithoutOptions_ShouldReturnOnlyActiveDatasetSchema() {
    // Arrange
    String organizationId = UUID.randomUUID().toString();
    Scan scan =
        new Scan(new Key(new TextValue(DataSetSchema.COMPANY_ID, MOCK_COMPANY_ID)))
            .forNamespace(AppConfig.NAMESPACE)
            .forTable(DataSetSchema.TABLE_NAME)
            .withOrdering(new Scan.Ordering(DataSetSchema.CREATED_AT, Scan.Ordering.Order.DESC));
    List<DataSetSchema> org1DataSetSchemas = prepareDatasetSchemas("org1");
    List<DataSetSchema> org2DataSetSchemas = prepareDatasetSchemas("org2");
    List<DataSetSchema> allDataSetSchemas = new ArrayList<>(org1DataSetSchemas);
    allDataSetSchemas.addAll(org2DataSetSchemas);
    when(repository.scan(scan)).thenReturn(allDataSetSchemas);

    // Act
    List<DataSetSchema> result = service.readAllWithOrganizationId(MOCK_COMPANY_ID, "org1", null);

    // Assert
    verify(repository).scan(scan);
    Assertions.assertThat(result)
        .isEqualTo(
            org1DataSetSchemas.stream()
                .filter(DataSetSchema::isActive)
                .collect(Collectors.toList()));
  }

  @Test
  public void readAllWithOrganizationId_WithOptions_ShouldReturnOnlyActiveDatasetSchema() {
    // Arrange
    int start = 1;
    int end = 2;
    ReadAllDataSetSchemasRequestBody options =
        ReadAllDataSetSchemasRequestBody.builder().start(start).end(end).inactive(true).build();
    Scan scan =
        new Scan(new Key(new TextValue(DataSetSchema.COMPANY_ID, MOCK_COMPANY_ID)))
            .forNamespace(AppConfig.NAMESPACE)
            .forTable(DataSetSchema.TABLE_NAME)
            .withStart(new Key(new BigIntValue(DataSetSchema.CREATED_AT, start)))
            .withEnd(new Key(new BigIntValue(DataSetSchema.CREATED_AT, end)))
            .withOrdering(new Scan.Ordering(DataSetSchema.CREATED_AT, Scan.Ordering.Order.DESC));
    List<DataSetSchema> org1DataSetSchemas = prepareDatasetSchemas("org1");
    List<DataSetSchema> org2DataSetSchemas = prepareDatasetSchemas("org2");
    List<DataSetSchema> allDataSetSchemas = new ArrayList<>(org1DataSetSchemas);
    allDataSetSchemas.addAll(org2DataSetSchemas);
    when(repository.scan(scan)).thenReturn(allDataSetSchemas);

    // Act
    List<DataSetSchema> result =
        service.readAllWithOrganizationId(MOCK_COMPANY_ID, "org2", options);

    // Assert
    verify(repository).scan(scan);
    Assertions.assertThat(result).isEqualTo(org2DataSetSchemas);
  }

  @Test
  public void read_WithCorrectArguments_ShouldReturnOnlyActiveDatasetSchema() {
    // Arrange
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.createObjectNode();
    ((ObjectNode) node).put("mocked", "jsonNode");
    DataSetSchema dataSetSchema =
        DataSetSchema.builder()
            .companyId(MOCK_COMPANY_ID)
            .createdAt(2)
            .organizationId(UUID.randomUUID().toString())
            .description("bar")
            .dataLocation(
                DataLocation.builder()
                    .uri("https:\\/\\/example.com\\/pds\\/uuid")
                    .connect("access_token")
                    .authenticate("oauth server")
                    .build())
            .categoryOfData("PII")
            .dataType("GPS data")
            .classification("Purchase History")
            .dataSetSchema(node)
            .active(true)
            .createdBy(UUID.randomUUID().toString())
            .build();
    Get get =
        new Get(
                new Key(new TextValue(DataSetSchema.COMPANY_ID, dataSetSchema.getCompanyId())),
                new Key(
                    new BigIntValue(DataSetSchema.CREATED_AT, dataSetSchema.getCreatedAt()),
                    new TextValue(ORGANIZATION_ID, dataSetSchema.getOrganizationId())))
            .forNamespace(AppConfig.NAMESPACE)
            .forTable(DataSetSchema.TABLE_NAME);
    when(repository.get(get)).thenReturn(Optional.of(dataSetSchema));

    // Act
    DataSetSchema result =
        service.read(
            dataSetSchema.getCompanyId(),
            dataSetSchema.getOrganizationId(),
            dataSetSchema.getCreatedAt());

    // Assert
    verify(repository).get(get);
    Assertions.assertThat(result).isEqualTo(dataSetSchema);
  }

  private List<DataSetSchema> prepareDatasetSchemas(String organizationId) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.createObjectNode();
    ((ObjectNode) node).put("mocked", "jsonNode");
    return Arrays.asList(
        DataSetSchema.builder()
            .companyId(MOCK_COMPANY_ID)
            .createdAt(2)
            .organizationId(organizationId)
            .description("bar")
            .dataLocation(
                DataLocation.builder()
                    .uri("https:\\/\\/example.com\\/pds\\/uuid")
                    .connect("access_token")
                    .authenticate("oauth server")
                    .build())
            .categoryOfData("PII")
            .dataType("GPS data")
            .classification("Purchase History")
            .dataSetSchema(node)
            .active(true)
            .createdBy(UUID.randomUUID().toString())
            .updatedAt(4)
            .build(),
        DataSetSchema.builder()
            .companyId(MOCK_COMPANY_ID)
            .createdAt(2)
            .organizationId(organizationId)
            .description("bar")
            .dataLocation(
                DataLocation.builder()
                    .uri("https:\\/\\/example.com\\/pds\\/uuid")
                    .connect("access_token")
                    .authenticate("oauth server")
                    .build())
            .categoryOfData("PII")
            .dataType("GPS data")
            .classification("Purchase History")
            .dataSetSchema(node)
            .active(true)
            .createdBy(UUID.randomUUID().toString())
            .updatedAt(3)
            .build());
  }
}
