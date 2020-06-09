package com.scalar.ist.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.ist.api.controller.dto.ReadAllPurposesRequestBody;
import com.scalar.ist.api.mock.security_context.MockAuthenticatedUser;
import com.scalar.ist.api.model.Purpose;
import com.scalar.ist.api.service.PurposesService;
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
@WebMvcTest({PurposesController.class})
public class PurposesControllerTest {
  private static final String PATH = "/cmp/purposes";
  private static final String PATH_WITH_SLASH = PATH + "/";
  private static final String MOCK_HOLDER_ID = "0b2db528-1938-4ab7-90e0-b19c36270eef";
  private static final String MOCK_COMPANY_ID = "scalar-labs";
  @MockBean PurposesService purposesService;
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
    List<Purpose> purposes = preparePurposes(MOCK_COMPANY_ID, UUID.randomUUID().toString());
    ReadAllPurposesRequestBody requestBody =
        ReadAllPurposesRequestBody.builder().start(1).end(5).inactive(false).build();
    when(purposesService.readAll(MOCK_COMPANY_ID, requestBody)).thenReturn(purposes);

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
        .isEqualTo(objectMapper.writeValueAsString(purposes));
    verify(purposesService).readAll(MOCK_COMPANY_ID, requestBody);
  }

  @Test
  @MockAuthenticatedUser(holderId = MOCK_HOLDER_ID, companyId = MOCK_COMPANY_ID)
  public void readAll_WithoutParams_ShouldBeSuccessful() throws Exception {
    List<Purpose> purposes = preparePurposes(MOCK_COMPANY_ID, UUID.randomUUID().toString());
    // Arrange
    when(purposesService.readAll(MOCK_COMPANY_ID, null)).thenReturn(purposes);

    // Act
    MvcResult jsonResult =
        mockMvc
            .perform(get(PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // Assert
    assertThat(jsonResult.getResponse().getContentAsString())
        .isEqualTo(objectMapper.writeValueAsString(purposes));
    verify(purposesService).readAll(MOCK_COMPANY_ID, null);
  }

  @Test
  @MockAuthenticatedUser(holderId = MOCK_HOLDER_ID, companyId = MOCK_COMPANY_ID)
  public void readAllWithOrganizationId_WithParams_ShouldBeSuccessful() throws Exception {
    // Arrange
    String organizationId = "org1";
    List<Purpose> purposes = preparePurposes(MOCK_COMPANY_ID, organizationId);
    ReadAllPurposesRequestBody requestBody =
        ReadAllPurposesRequestBody.builder().start(1).end(5).inactive(false).build();
    when(purposesService.readAllWithOrganizationId(MOCK_COMPANY_ID, organizationId, requestBody))
        .thenReturn(purposes);

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
        .isEqualTo(objectMapper.writeValueAsString(purposes));
    verify(purposesService).readAllWithOrganizationId(MOCK_COMPANY_ID, organizationId, requestBody);
  }

  @Test
  @MockAuthenticatedUser(holderId = MOCK_HOLDER_ID, companyId = MOCK_COMPANY_ID)
  public void readAllWithOrganizationId_WithoutParams_ShouldBeSuccessful() throws Exception {
    // Arrange
    String organizationId = "org1";
    List<Purpose> purposes = preparePurposes(MOCK_COMPANY_ID, organizationId);
    when(purposesService.readAllWithOrganizationId(MOCK_COMPANY_ID, organizationId, null))
        .thenReturn(purposes);

    // Act
    MvcResult jsonResult =
        mockMvc
            .perform(get(PATH_WITH_SLASH + organizationId).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // Assert
    assertThat(jsonResult.getResponse().getContentAsString())
        .isEqualTo(objectMapper.writeValueAsString(purposes));
    verify(purposesService).readAllWithOrganizationId(MOCK_COMPANY_ID, organizationId, null);
  }

  @Test
  @MockAuthenticatedUser(holderId = MOCK_HOLDER_ID, companyId = MOCK_COMPANY_ID)
  public void read_WithCorrectArguments_ShouldBeSuccessful() throws Exception {
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
    when(purposesService.read(
            purpose.getCompanyId(), purpose.getOrganizationId(), purpose.getCreatedAt()))
        .thenReturn(purpose);

    // Act
    MvcResult jsonResult =
        mockMvc
            .perform(
                get(PATH_WITH_SLASH + purpose.getOrganizationId() + "/" + purpose.getCreatedAt())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // Assert
    assertThat(jsonResult.getResponse().getContentAsString())
        .isEqualTo(objectMapper.writeValueAsString(purpose));
    verify(purposesService)
        .read(purpose.getCompanyId(), purpose.getOrganizationId(), purpose.getCreatedAt());
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
            .createdAt(3)
            .organizationId(organizationId)
            .categoryOfPurpose("marketing/customer_service")
            .purposeName("marketingGoal_2")
            .description("bar")
            .legalText("lorem ipsum")
            .userFriendlyText("foo")
            .guidance("zoo")
            .note("none")
            .active(true)
            .createdBy(UUID.randomUUID().toString())
            .updatedAt(4)
            .build());
  }
}
