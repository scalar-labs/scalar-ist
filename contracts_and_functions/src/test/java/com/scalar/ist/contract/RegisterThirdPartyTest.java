package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ADDRESS;
import static com.scalar.ist.common.Constants.COMPANY_EMAIL;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CORPORATE_NUMBER;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_NOT_MATCHED;
import static com.scalar.ist.common.Constants.IS_ACTIVE;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.ORGANIZATION_DESCRIPTION;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_NAME;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.THIRD_PARTY_DOMAIN;
import static com.scalar.ist.common.Constants.THIRD_PARTY_ID;
import static com.scalar.ist.common.Constants.THIRD_PARTY_METADATA;
import static com.scalar.ist.common.Constants.THIRD_PARTY_NAME;
import static com.scalar.ist.common.Constants.USER_PROFILE_ROLES;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.VALIDATE_PERMISSION;
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
import com.scalar.ist.common.Constants;
import com.scalar.ist.util.Util;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RegisterThirdPartyTest {
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final String SCHEMA_FILENAME = "register_third_party.json";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final String MOCKED_THIRD_PARTY_NAME = "thirdparty, inc";
  private static final String MOCKED_CORPORATE_NUMBER = "1234567890123";
  private static final long MOCKED_TIMESTAMP = 1L;
  private static final JsonArray MOCKED_ARRAY_STRING_ROLES =
      Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build();
  private static final String MOCKED_ASSET_NAME = "third_party";
  private final JsonObject MOCKED_THIRD_PARTY_METADATA = prepareThirdPartyMetaData();
  private final JsonArray MOCKED_ORGANIZATIONS = prepareOrganizationsArray();
  private final JsonArray organizationIds =
      Json.createArrayBuilder()
          .add(MOCKED_ORGANIZATION_ID)
          .add(UUID.randomUUID().toString())
          .build();
  @Mock Ledger ledger;
  CertificateEntry.Key key = new CertificateEntry.Key(MOCKED_HOLDER_ID, 1);
  private RegisterThirdParty registerThirdParty;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    registerThirdParty = spy(new RegisterThirdParty());
  }

  @Test
  public void invoke_ProperArgumentsAndPropertiesGiven_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile();
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(userProfile, argument);
    JsonObject putRecordArgument = preparePutRecordArgument(argument, properties);
    JsonObject validationArgument = prepareValidationArgument(argument, properties);
    when(registerThirdParty.getCertificateKey()).thenReturn(key);
    doReturn(userProfile)
        .when(registerThirdParty)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(registerThirdParty)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    doReturn(null)
        .when(registerThirdParty)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(null)
        .when(registerThirdParty)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validationArgument);

    // Act
    registerThirdParty.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(registerThirdParty).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(registerThirdParty).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(registerThirdParty).invokeSubContract(VALIDATE_ARGUMENT, ledger, validationArgument);
    verify(registerThirdParty)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(registerThirdParty, times(4)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithWrongHolderId_ShouldThrowContractContextException() {
    // arrange
    JsonObject propertiesWithWrongHolderId =
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
    JsonObject argument = prepareArgument();
    JsonObject validationArgument =
        prepareValidationArgument(argument, propertiesWithWrongHolderId);
    when(registerThirdParty.getCertificateKey()).thenReturn(key);
    doReturn(null)
        .when(registerThirdParty)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validationArgument);

    // act
    // assert
    assertThatThrownBy(
            () -> {
              registerThirdParty.invoke(ledger, argument, Optional.of(propertiesWithWrongHolderId));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_NOT_MATCHED);
    verify(registerThirdParty).invokeSubContract(VALIDATE_ARGUMENT, ledger, validationArgument);
    verify(registerThirdParty).invokeSubContract(any(), any(), any());
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
              registerThirdParty.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(registerThirdParty, never()).invokeSubContract(any(), any(), any());
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
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              registerThirdParty.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(registerThirdParty, never()).invokeSubContract(any(), any(), any());
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
              registerThirdParty.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NAME_IS_MISSING);
    verify(registerThirdParty, never()).invokeSubContract(any(), any(), any());
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }

  private JsonObject prepareArgument() {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(THIRD_PARTY_DOMAIN, MOCKED_ORGANIZATION_ID)
        .add(THIRD_PARTY_NAME, MOCKED_THIRD_PARTY_NAME)
        .add(CORPORATE_NUMBER, MOCKED_CORPORATE_NUMBER)
        .add(THIRD_PARTY_METADATA, MOCKED_THIRD_PARTY_METADATA)
        .add(ORGANIZATIONS, MOCKED_ORGANIZATIONS)
        .add(CREATED_AT, MOCKED_TIMESTAMP)
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

  private JsonObject preparePutRecordArgument(JsonObject argument, JsonObject properties) {
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId =
        String.join(
            "-", assetName, argument.getString(COMPANY_ID), argument.getString(THIRD_PARTY_DOMAIN));

    JsonObject data =
        Json.createObjectBuilder()
            .add(THIRD_PARTY_ID, assetId)
            .add(COMPANY_ID, argument.getString(COMPANY_ID))
            .add(THIRD_PARTY_DOMAIN, argument.getString(THIRD_PARTY_DOMAIN))
            .add(THIRD_PARTY_NAME, argument.getString(THIRD_PARTY_NAME))
            .add(CORPORATE_NUMBER, argument.getString(CORPORATE_NUMBER))
            .add(THIRD_PARTY_METADATA, argument.getJsonObject(THIRD_PARTY_METADATA))
            .add(ORGANIZATIONS, argument.getJsonArray(ORGANIZATIONS))
            .build();

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_DATA, data)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, argument.getJsonNumber(CREATED_AT))
        .build();
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_COMPANY_ID).build();
  }

  private JsonObject prepareUserProfile() {
    return Json.createObjectBuilder()
        .add(ORGANIZATION_IDS, organizationIds)
        .add(ROLES, MOCKED_ARRAY_STRING_ROLES)
        .build();
  }

  private JsonArray prepareOrganizationsArray() {
    JsonObject organization1 =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
            .add(ORGANIZATION_NAME, "my_organization")
            .add(ORGANIZATION_DESCRIPTION, "sample org")
            .add(IS_ACTIVE, true)
            .build();
    JsonObject organization2 =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, UUID.randomUUID().toString())
            .add(ORGANIZATION_NAME, "my_other_organization")
            .add(ORGANIZATION_DESCRIPTION, "sample org")
            .add(IS_ACTIVE, true)
            .build();

    return Json.createArrayBuilder().add(organization1).add(organization2).build();
  }

  private JsonObject preparePermissionValidationArgument(
      JsonObject userProfile, JsonObject arguments) {
    JsonArray ROLES = Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build();
    return Json.createObjectBuilder()
        .add(ROLES_REQUIRED, ROLES)
        .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
        .build();
  }

  private JsonObject prepareThirdPartyMetaData() {
    return Json.createObjectBuilder()
        .add(THIRD_PARTY_NAME, MOCKED_THIRD_PARTY_NAME)
        .add(COMPANY_ADDRESS, "Shinjuku, Tokyo")
        .add(COMPANY_EMAIL, "mail@thirdparty.com")
        .build();
  }
}
