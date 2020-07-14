package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ADDRESS;
import static com.scalar.ist.common.Constants.COMPANY_EMAIL;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.COMPANY_METADATA;
import static com.scalar.ist.common.Constants.COMPANY_NAME;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_NOT_MATCHED;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.ORGANIZATION_DESCRIPTION;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_NAME;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_UPSERT;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.THIRD_PARTY_NAME;
import static com.scalar.ist.common.Constants.UPDATED_AT;
import static com.scalar.ist.common.Constants.USER_PROFILE_ROLES;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.VALIDATE_PERMISSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.dl.ledger.crypto.CertificateEntry;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpsertOrganizationTest {
  private static final String SCHEMA_FILENAME = "upsert_organization.json";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_ASSET_NAME = "organization";
  private static final String MOCKED_COMPANY_NAME = "scalar, inc.";
  private static final String MOCKED_CORPORATE_NUMBER = "1234567890123";
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final String MOCKED_ORGANIZATION_NAME = "sample organization";
  private static final String MOCKED_ORGANIZATION_DESCRIPTION = "sample description";
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(Long.MAX_VALUE);
  private static final JsonNumber MOCKED_UPDATED_AT = Json.createValue(2);
  @Mock private Ledger ledger;
  @Mock private CertificateEntry.Key certificateKey;
  private UpsertOrganization upsertOrganization;

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    upsertOrganization = spy(new UpsertOrganization());
  }

  @Test
  public void invoke_WithExistingOrganizationId_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile();
    JsonObject getAssetRecordArgument = prepareGetAssetRecordArgument(argument, properties);
    JsonObject company = prepareCompany(MOCKED_ORGANIZATION_ID);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    JsonObject validateRolesArgument = prepareValidateRolesArgument();
    JsonObject putRecordArgument = preparePutRecordArgument(argument, properties, company);
    when(upsertOrganization.getCertificateKey()).thenReturn(certificateKey);
    when(upsertOrganization.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(userProfile)
        .when(upsertOrganization)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(company)
        .when(upsertOrganization)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    doReturn(null)
        .when(upsertOrganization)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(null)
        .when(upsertOrganization)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, validateRolesArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(upsertOrganization)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);

    // Act
    upsertOrganization.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(upsertOrganization).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(upsertOrganization).invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(upsertOrganization)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertOrganization)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, validateRolesArgument);
    verify(upsertOrganization).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(upsertOrganization, times(5)).invokeSubContract(any(), any(), any());
    assertThat(putRecordArgument.getJsonObject(RECORD_DATA).getJsonArray(ORGANIZATIONS).size())
        .isEqualTo(3);
  }

  @Test
  public void invoke_PropertyWithoutSchema_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = Json.createObjectBuilder().add(HOLDER_ID, MOCKED_HOLDER_ID).build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              upsertOrganization.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(upsertOrganization, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutHolderId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                    .build())
            .add(ASSET_NAME, MOCKED_ASSET_NAME)
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              upsertOrganization.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(upsertOrganization, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutAssetName_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                    .build())
            .add(HOLDER_ID, MOCKED_HOLDER_ID)
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              upsertOrganization.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NAME_IS_MISSING);
    verify(upsertOrganization, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithNonExistingOrganizationId_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile();
    JsonObject getAssetRecordArgument = prepareGetAssetRecordArgument(argument, properties);
    JsonObject company = prepareCompany(UUID.randomUUID().toString());
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    JsonObject validateRolesArgument = prepareValidateRolesArgument();
    JsonObject putRecordArgument = preparePutRecordArgument(argument, properties, company);
    when(upsertOrganization.getCertificateKey()).thenReturn(certificateKey);
    when(upsertOrganization.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(userProfile)
        .when(upsertOrganization)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(company)
        .when(upsertOrganization)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    doReturn(null)
        .when(upsertOrganization)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(null)
        .when(upsertOrganization)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, validateRolesArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(upsertOrganization)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);

    // Act
    upsertOrganization.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(upsertOrganization).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(upsertOrganization).invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(upsertOrganization)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertOrganization)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, validateRolesArgument);
    verify(upsertOrganization).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(upsertOrganization, times(5)).invokeSubContract(any(), any(), any());
    assertThat(putRecordArgument.getJsonObject(RECORD_DATA).getJsonArray(ORGANIZATIONS).size())
        .isEqualTo(4);
  }

  @Test
  public void invoke_WithWrongHolderId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                    .build())
            .add(HOLDER_ID, UUID.randomUUID().toString())
            .add(ASSET_NAME, MOCKED_ASSET_NAME)
            .build();
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(upsertOrganization.getCertificateKey()).thenReturn(certificateKey);
    when(upsertOrganization.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(null)
        .when(upsertOrganization)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              upsertOrganization.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_NOT_MATCHED);
    verify(upsertOrganization)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertOrganization).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithMissingHolderIdProperty_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                    .build())
            .build();
    when(upsertOrganization.getCertificateKey()).thenReturn(certificateKey);
    when(upsertOrganization.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              upsertOrganization.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(upsertOrganization, never()).invokeSubContract(any(), any(), any());
  }

  private JsonObject prepareArgument() {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
        .add(ORGANIZATION_NAME, MOCKED_ORGANIZATION_NAME)
        .add(ORGANIZATION_DESCRIPTION, MOCKED_ORGANIZATION_DESCRIPTION)
        .add(IS_ACTIVE, true)
        .add(CREATED_AT, MOCKED_CREATED_AT)
        .add(UPDATED_AT, MOCKED_UPDATED_AT)
        .build();
  }

  private JsonObject prepareProperties() {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                .build())
        .add(HOLDER_ID, MOCKED_HOLDER_ID)
        .add(ASSET_NAME, MOCKED_ASSET_NAME)
        .build();
  }

  private JsonObject prepareCompanyMetadata() {
    return Json.createObjectBuilder()
        .add(THIRD_PARTY_NAME, "scalar, inc")
        .add(COMPANY_ADDRESS, "Shinjuku")
        .add(COMPANY_EMAIL, "mail@example.com")
        .build();
  }

  private JsonObject prepareGetAssetRecordArgument(JsonObject argument, JsonObject properties) {
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId = String.join("-", assetName, argument.getString(COMPANY_ID));

    return Json.createObjectBuilder().add(ASSET_ID, assetId).add(RECORD_IS_HASHED, false).build();
  }

  private JsonObject prepareCompany(String organizationId) {
    return Json.createObjectBuilder()
        .add(COMPANY_NAME, MOCKED_COMPANY_NAME)
        .add(CORPORATE_NUMBER, MOCKED_CORPORATE_NUMBER)
        .add(COMPANY_METADATA, prepareCompanyMetadata())
        .add(ORGANIZATIONS, prepareOrganizationsArray(organizationId))
        .build();
  }

  private JsonArray prepareOrganizationsArray(String organizationId) {
    JsonObject org1 =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, organizationId)
            .add(ORGANIZATION_NAME, "organization1")
            .add(ORGANIZATION_DESCRIPTION, "org1desc")
            .add(IS_ACTIVE, true)
            .build();
    JsonObject org2 =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, UUID.randomUUID().toString())
            .add(ORGANIZATION_NAME, "organization2")
            .add(ORGANIZATION_DESCRIPTION, "org2desc")
            .add(IS_ACTIVE, false)
            .build();
    JsonObject org3 =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, UUID.randomUUID().toString())
            .add(ORGANIZATION_NAME, "organization3")
            .add(ORGANIZATION_DESCRIPTION, "org3desc")
            .add(IS_ACTIVE, true)
            .build();
    return Json.createArrayBuilder().add(org1).add(org2).add(org3).build();
  }

  private JsonObject preparePutRecordArgument(
      JsonObject argument, JsonObject properties, JsonObject company) {
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId = String.join("-", assetName, argument.getString(COMPANY_ID));

    JsonObject organization =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, argument.getString(ORGANIZATION_ID))
            .add(ORGANIZATION_NAME, argument.getString(ORGANIZATION_NAME))
            .add(ORGANIZATION_DESCRIPTION, argument.getString(ORGANIZATION_DESCRIPTION))
            .add(IS_ACTIVE, argument.getBoolean(IS_ACTIVE))
            .build();

    List<JsonObject> organizations = new ArrayList<>();
    boolean orgExists = false;
    for (JsonObject currentOrganization :
        company.getJsonArray(ORGANIZATIONS).getValuesAs(JsonValue::asJsonObject)) {
      if (currentOrganization
          .getString(ORGANIZATION_ID)
          .equals(argument.getString(ORGANIZATION_ID))) {
        currentOrganization = organization;
        orgExists = true;
      }
      organizations.add(currentOrganization);
    }
    if (!orgExists) {
      organizations.add(organization);
    }

    JsonArrayBuilder organizationsBuilder = Json.createArrayBuilder();
    for (JsonObject org : organizations) {
      organizationsBuilder.add(org);
    }

    JsonObject data =
        Json.createObjectBuilder()
            .add(COMPANY_ID, argument.getString(COMPANY_ID))
            .add(COMPANY_NAME, company.getString(COMPANY_NAME))
            .add(CORPORATE_NUMBER, company.getString(CORPORATE_NUMBER))
            .add(COMPANY_METADATA, company.getJsonObject(COMPANY_METADATA))
            .add(ORGANIZATIONS, organizationsBuilder.build())
            .build();

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_MODE, RECORD_MODE_UPSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, argument.getJsonNumber(UPDATED_AT))
        .build();
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_COMPANY_ID).build();
  }

  private JsonObject prepareUserProfile() {
    return Json.createObjectBuilder()
        .add(ROLES, Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build())
        .build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }

  private JsonObject prepareValidateRolesArgument() {
    return Json.createObjectBuilder()
        .add(USER_PROFILE_ROLES, Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build())
        .add(ROLES_REQUIRED, Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build())
        .build();
  }
}
