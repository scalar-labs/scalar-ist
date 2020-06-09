package com.scalar.ist.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scalar.ist.api.controller.dto.ReadAllDataSetSchemasRequestBody;
import com.scalar.ist.api.mock.security_context.MockAuthenticatedUser;
import com.scalar.ist.api.model.DataSetSchema;
import com.scalar.ist.api.model.DataSetSchema.DataLocation;
import com.scalar.ist.api.service.DataSetSchemasService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@WebMvcTest({DataSetSchemasController.class})
public class DataSetSchemasControllerTest {
  private static final String PATH = "/cmp/datasetschemas";
  private static final String PATH_WITH_SLASH = PATH + "/";
  private static final String MOCK_HOLDER_ID = "0b2db528-1938-4ab7-90e0-b19c36270eef";
  private static final String MOCK_COMPANY_ID = "scalar-labs";
  @MockBean DataSetSchemasService DataSetSchemasService;
  @Autowired WebApplicationContext webApplicationContext;
  @Autowired private ObjectMapper objectMapper;
  private MockMvc mockMvc;

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  @MockAuthenticatedUser(holderId = MOCK_HOLDER_ID, companyId = MOCK_COMPANY_ID)
  public void readAll_WithParams_ShouldBeSuccessful() throws Exception {
    // Arrange
    List<DataSetSchema> dataSetSchemas =
        prepareDatasetSchema(MOCK_COMPANY_ID, UUID.randomUUID().toString());
    ReadAllDataSetSchemasRequestBody requestBody =
        ReadAllDataSetSchemasRequestBody.builder().start(1).end(5).inactive(false).build();
    when(DataSetSchemasService.readAll(MOCK_COMPANY_ID, requestBody)).thenReturn(dataSetSchemas);

    // Act
    MvcResult jsonResult =
        mockMvc
            .perform(
                get(PATH)
                    .content(objectMapper.writeValueAsString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // Assert
    assertThat(jsonResult.getResponse().getContentAsString())
        .isEqualTo(objectMapper.writeValueAsString(dataSetSchemas));
    verify(DataSetSchemasService).readAll(MOCK_COMPANY_ID, requestBody);
  }

  @Test
  @MockAuthenticatedUser(holderId = MOCK_HOLDER_ID, companyId = MOCK_COMPANY_ID)
  public void readAll_WithoutParams_ShouldBeSuccessful() throws Exception {
    List<DataSetSchema> dataSetSchemas =
        prepareDatasetSchema(MOCK_COMPANY_ID, UUID.randomUUID().toString());
    // Arrange
    when(DataSetSchemasService.readAll(MOCK_COMPANY_ID, null)).thenReturn(dataSetSchemas);

    // Act
    MvcResult jsonResult =
        mockMvc
            .perform(get(PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // Assert
    assertThat(jsonResult.getResponse().getContentAsString())
        .isEqualTo(objectMapper.writeValueAsString(dataSetSchemas));
    verify(DataSetSchemasService).readAll(MOCK_COMPANY_ID, null);
  }

  @Test
  @MockAuthenticatedUser(holderId = MOCK_HOLDER_ID, companyId = MOCK_COMPANY_ID)
  public void readAllWithOrganizationId_WithParams_ShouldBeSuccessful() throws Exception {
    // Arrange
    String organizationId = "org1";
    List<DataSetSchema> dataSetSchemas = prepareDatasetSchema(MOCK_COMPANY_ID, organizationId);
    ReadAllDataSetSchemasRequestBody requestBody =
        ReadAllDataSetSchemasRequestBody.builder().start(1).end(5).inactive(false).build();
    when(DataSetSchemasService.readAllWithOrganizationId(
            MOCK_COMPANY_ID, organizationId, requestBody))
        .thenReturn(dataSetSchemas);

    // Act
    MvcResult jsonResult =
        mockMvc
            .perform(
                get(PATH_WITH_SLASH + organizationId)
                    .content(objectMapper.writeValueAsString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // Assert
    assertThat(jsonResult.getResponse().getContentAsString())
        .isEqualTo(objectMapper.writeValueAsString(dataSetSchemas));
    verify(DataSetSchemasService)
        .readAllWithOrganizationId(MOCK_COMPANY_ID, organizationId, requestBody);
  }

  @Test
  @MockAuthenticatedUser(holderId = MOCK_HOLDER_ID, companyId = MOCK_COMPANY_ID)
  public void readAllWithOrganizationId_WithoutParams_ShouldBeSuccessful() throws Exception {
    // Arrange
    String organizationId = "org1";
    List<DataSetSchema> dataSetSchemas = prepareDatasetSchema(MOCK_COMPANY_ID, organizationId);
    when(DataSetSchemasService.readAllWithOrganizationId(MOCK_COMPANY_ID, organizationId, null))
        .thenReturn(dataSetSchemas);

    // Act
    MvcResult jsonResult =
        mockMvc
            .perform(get(PATH_WITH_SLASH + organizationId).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // Assert
    assertThat(jsonResult.getResponse().getContentAsString())
        .isEqualTo(objectMapper.writeValueAsString(dataSetSchemas));
    verify(DataSetSchemasService).readAllWithOrganizationId(MOCK_COMPANY_ID, organizationId, null);
  }

  @Test
  @MockAuthenticatedUser(holderId = MOCK_HOLDER_ID, companyId = MOCK_COMPANY_ID)
  public void read_WithCorrectArguments_ShouldBeSuccessful() throws Exception {
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
    when(DataSetSchemasService.read(
            dataSetSchema.getCompanyId(),
            dataSetSchema.getOrganizationId(),
            dataSetSchema.getCreatedAt()))
        .thenReturn(dataSetSchema);

    // Act
    MvcResult jsonResult =
        mockMvc
            .perform(
                get(PATH_WITH_SLASH
                        + dataSetSchema.getOrganizationId()
                        + "/"
                        + dataSetSchema.getCreatedAt())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // Assert
    assertThat(jsonResult.getResponse().getContentAsString())
        .isEqualTo(objectMapper.writeValueAsString(dataSetSchema));
    verify(DataSetSchemasService)
        .read(
            dataSetSchema.getCompanyId(),
            dataSetSchema.getOrganizationId(),
            dataSetSchema.getCreatedAt());
  }

  private List<DataSetSchema> prepareDatasetSchema(String companyId, String organizationId) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.createObjectNode();
    ((ObjectNode) node).put("mocked", "jsonNode");
    return Arrays.asList(
        DataSetSchema.builder()
            .companyId(companyId)
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
            .build(),
        DataSetSchema.builder()
            .companyId(companyId)
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
