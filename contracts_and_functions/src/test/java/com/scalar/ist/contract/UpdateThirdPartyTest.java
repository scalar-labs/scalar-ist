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
import static com.scalar.ist.common.Constants.RECORD_MODE_UPDATE;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.THIRD_PARTY_DOMAIN;
import static com.scalar.ist.common.Constants.THIRD_PARTY_ID;
import static com.scalar.ist.common.Constants.THIRD_PARTY_METADATA;
import static com.scalar.ist.common.Constants.THIRD_PARTY_NAME;
import static com.scalar.ist.common.Constants.UPDATED_AT;
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
import com.scalar.ist.util.Util;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpdateThirdPartyTest {
  private static final String SCHEMA_FILENAME = "update_third_party.json";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_THIRD_PARTY_DOMAIN = "thirdparty.com";
  private static final String MOCKED_THIRD_PARTY_NAME = "Third Party, Inc.";
  private static final String MOCKED_ASSET_NAME = "third_party";
  private static final JsonObject MOCKED_THIRD_PARTY_METADATA =
      Json.createObjectBuilder()
          .add(THIRD_PARTY_NAME, MOCKED_THIRD_PARTY_NAME)
          .add(COMPANY_ADDRESS, "Shinjuku")
          .add(COMPANY_EMAIL, "mail@sample.com")
          .build();
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(Long.MAX_VALUE);
  private static final JsonNumber MOCKED_UPDATED_AT = Json.createValue(2);
  @Mock private Ledger ledger;
  @Mock private CertificateEntry.Key certificateKey;
  private UpdateThirdParty updateThirdParty;

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    updateThirdParty = spy(new UpdateThirdParty());
  }

  @Test
  public void invoke_ProperArgumentsAndPropertiesGiven_ShouldPass() {
    // arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties(MOCKED_HOLDER_ID);
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile();
    JsonObject getAssetRecordArgument = prepareGetAssetRecordArgument();
    JsonObject thirdPartyAsset = prepareThirdPartyAsset();
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    JsonObject validatePermissionArgument = prepareValidatePermissionArgument();
    JsonObject putRecordArgument = preparePutRecordArgument(argument, properties, thirdPartyAsset);
    when(updateThirdParty.getCertificateKey()).thenReturn(certificateKey);
    when(updateThirdParty.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(userProfile)
        .when(updateThirdParty)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(thirdPartyAsset)
        .when(updateThirdParty)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    doReturn(null)
        .when(updateThirdParty)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(null)
        .when(updateThirdParty)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, validatePermissionArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(updateThirdParty)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);

    // act
    updateThirdParty.invoke(ledger, argument, Optional.of(properties));

    // assert
    verify(updateThirdParty).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(updateThirdParty).invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(updateThirdParty).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(updateThirdParty)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, validatePermissionArgument);
    verify(updateThirdParty).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(updateThirdParty, times(5)).invokeSubContract(any(), any(), any());
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
              updateThirdParty.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(updateThirdParty, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutHolderId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties =
        Json.createObjectBuilder()
            .add(CONTRACT_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              updateThirdParty.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(updateThirdParty, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutAssetName_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties =
        Json.createObjectBuilder()
            .add(CONTRACT_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
            .add(HOLDER_ID, MOCKED_HOLDER_ID)
            .build();

    // Act
    // Assert
    assertThatThrownBy(
        () -> {
          updateThirdParty.invoke(ledger, argument, Optional.of(properties));
        })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NAME_IS_MISSING);
    verify(updateThirdParty, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithWrongHolderId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties(UUID.randomUUID().toString());
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(updateThirdParty.getCertificateKey()).thenReturn(certificateKey);
    when(updateThirdParty.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(null)
        .when(updateThirdParty)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              updateThirdParty.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_NOT_MATCHED);
    verify(updateThirdParty).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(updateThirdParty).invokeSubContract(any(), any(), any());
  }

  private JsonObject preparePutRecordArgument(JsonObject argument, JsonObject properties, JsonObject thirdParty) {
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId =
        String.join(
            "-",
            assetName,
            argument.getString(COMPANY_ID),
            argument.getString(THIRD_PARTY_DOMAIN));

    JsonObjectBuilder data =
        Json.createObjectBuilder()
            .add(THIRD_PARTY_ID, thirdParty.getString(THIRD_PARTY_ID))
            .add(COMPANY_ID, thirdParty.getString(COMPANY_ID))
            .add(THIRD_PARTY_DOMAIN, thirdParty.getString(THIRD_PARTY_DOMAIN))
            .add(THIRD_PARTY_NAME, argument.getString(THIRD_PARTY_NAME))
            .add(THIRD_PARTY_METADATA, argument.getJsonObject(THIRD_PARTY_METADATA))
            .add(ORGANIZATIONS, argument.getJsonArray(ORGANIZATIONS));
    if (argument.containsKey(CORPORATE_NUMBER)) {
      data.add(CORPORATE_NUMBER, argument.getString(CORPORATE_NUMBER));
    }

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data.build())
        .add(RECORD_MODE, RECORD_MODE_UPDATE)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, argument.getJsonNumber(UPDATED_AT))
        .build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }

  private JsonObject prepareValidatePermissionArgument() {
    return Json.createObjectBuilder()
        .add(USER_PROFILE_ROLES, Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build())
        .add(ROLES_REQUIRED, Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build())
        .build();
  }

  private JsonObject prepareThirdPartyAsset() {
    return Json.createObjectBuilder()
        .add(THIRD_PARTY_ID, UUID.randomUUID().toString())
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(THIRD_PARTY_DOMAIN, MOCKED_THIRD_PARTY_DOMAIN)
        .add(THIRD_PARTY_NAME, MOCKED_THIRD_PARTY_NAME)
        .build();
  }

  private JsonObject prepareArgument() {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(THIRD_PARTY_DOMAIN, MOCKED_THIRD_PARTY_DOMAIN)
        .add(CREATED_AT, MOCKED_CREATED_AT)
        .add(THIRD_PARTY_NAME, MOCKED_THIRD_PARTY_NAME)
        .add(THIRD_PARTY_METADATA, MOCKED_THIRD_PARTY_METADATA)
        .add(ORGANIZATIONS, prepareOrganizationsArray())
        .add(UPDATED_AT, MOCKED_UPDATED_AT)
        .build();
  }

  private JsonObject prepareProperties(String holderId) {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME)))
        .add(HOLDER_ID, holderId)
        .add(ASSET_NAME, MOCKED_ASSET_NAME)
        .build();
  }

  private JsonObject prepareGetAssetRecordArgument() {
    String assetId = String.join("-", MOCKED_ASSET_NAME, MOCKED_COMPANY_ID, MOCKED_THIRD_PARTY_DOMAIN);
    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_IS_HASHED, false)
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

  private JsonArray prepareOrganizationsArray() {
    JsonObject org1 =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, UUID.randomUUID().toString())
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
}
