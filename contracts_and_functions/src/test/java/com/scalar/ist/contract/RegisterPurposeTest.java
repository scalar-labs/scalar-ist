package com.scalar.ist.contract;

import com.scalar.dl.ledger.crypto.CertificateEntry.Key;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import com.scalar.ist.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import java.util.Optional;
import java.util.UUID;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_NOT_MATCHED;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
import static com.scalar.ist.common.Constants.PURPOSE_CATEGORY_OF_PURPOSE;
import static com.scalar.ist.common.Constants.PURPOSE_DESCRIPTION;
import static com.scalar.ist.common.Constants.PURPOSE_GUIDANCE;
import static com.scalar.ist.common.Constants.PURPOSE_ID;
import static com.scalar.ist.common.Constants.PURPOSE_LEGAL_TEXT;
import static com.scalar.ist.common.Constants.PURPOSE_NAME;
import static com.scalar.ist.common.Constants.PURPOSE_NOTE;
import static com.scalar.ist.common.Constants.PURPOSE_USER_FRIENDLY_TEXT;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
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

public class RegisterPurposeTest {
  private static final String MOCKED_ASSET_NAME = "purpose";
  private static final String SCHEMA_FILENAME = "register_purpose.json";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_PURPOSE_NAME = "Use the device storage";
  private static final String MOCKED_PURPOSE_DESCRIPTION =
      "Store or access information on your device";
  private static final String MOCKED_LEGAL_TEXT = "mocked legal text";
  private static final String MOCKED_USER_FRIENDLY_TEXT = "mocked user friendly text";
  private static final String MOCKED_GUIDANCE = "mocked guidance text";
  private static final String MOCKED_NOTE = "mocked note";
  private static final String MOCKED_CATEGORY_OF_PURPOSE = "Marketing";
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(Long.MAX_VALUE);
  private final JsonArray mockedOrganizationIds =
      Json.createArrayBuilder()
          .add(UUID.randomUUID().toString())
          .add(MOCKED_ORGANIZATION_ID)
          .build();
  @Mock private Ledger ledger;
  @Mock private Key certificateKey;
  private RegisterPurpose registerPurpose;

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    registerPurpose = spy(new RegisterPurpose());
  }

  @Test
  public void invoke_AsProcessor_ProperArgumentsAndPropertiesGiven_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile(ROLE_PROCESSOR);
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(userProfile, argument);
    JsonObject putRecordArgument = preparePutRecordArgument(argument, properties);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(registerPurpose.getCertificateKey()).thenReturn(certificateKey);
    when(registerPurpose.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(userProfile)
        .when(registerPurpose)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(registerPurpose)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    doReturn(null)
        .when(registerPurpose)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(null)
        .when(registerPurpose)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    registerPurpose.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(registerPurpose).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(registerPurpose).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(registerPurpose).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(registerPurpose)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(registerPurpose, times(4)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_AsController_ProperArgumentsAndPropertiesGiven_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile(ROLE_CONTROLLER);
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(userProfile, argument);
    JsonObject putRecordArgument = preparePutRecordArgument(argument, properties);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(registerPurpose.getCertificateKey()).thenReturn(certificateKey);
    when(registerPurpose.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(userProfile)
        .when(registerPurpose)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(registerPurpose)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    doReturn(null)
        .when(registerPurpose)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(null)
        .when(registerPurpose)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    registerPurpose.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(registerPurpose).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(registerPurpose).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(registerPurpose).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(registerPurpose)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(registerPurpose, times(4)).invokeSubContract(any(), any(), any());
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
              registerPurpose.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(registerPurpose, never()).invokeSubContract(any(), any(), any());
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
              registerPurpose.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(registerPurpose, never()).invokeSubContract(any(), any(), any());
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
          registerPurpose.invoke(ledger, argument, Optional.of(properties));
        })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NAME_IS_MISSING);
    verify(registerPurpose, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithWrongHolderId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument =
        Json.createObjectBuilder()
            .add(COMPANY_ID, MOCKED_COMPANY_ID)
            .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
            .add(PURPOSE_CATEGORY_OF_PURPOSE, MOCKED_CATEGORY_OF_PURPOSE)
            .add(PURPOSE_NAME, MOCKED_PURPOSE_NAME)
            .add(PURPOSE_LEGAL_TEXT, MOCKED_LEGAL_TEXT)
            .add(PURPOSE_USER_FRIENDLY_TEXT, MOCKED_USER_FRIENDLY_TEXT)
            .add(PURPOSE_GUIDANCE, MOCKED_GUIDANCE)
            .add(PURPOSE_NOTE, MOCKED_NOTE)
            .add(CREATED_AT, MOCKED_CREATED_AT)
            .build();
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
    JsonObject validateArgumentArgument =
        prepareValidationArgument(argument, propertiesWithWrongHolderId);
    when(registerPurpose.getCertificateKey()).thenReturn(certificateKey);
    when(registerPurpose.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(null)
        .when(registerPurpose)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              registerPurpose.invoke(ledger, argument, Optional.of(propertiesWithWrongHolderId));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_NOT_MATCHED);
    verify(registerPurpose).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(registerPurpose).invokeSubContract(any(), any(), any());
  }

  private JsonObject preparePutRecordArgument(JsonObject arguments, JsonObject properties) {
    JsonNumber createdAt = arguments.getJsonNumber(CREATED_AT);
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId =
        String.format(
            "%s-%s-%s", assetName, arguments.getString(ORGANIZATION_ID), createdAt.toString());
    JsonObject data =
        Json.createObjectBuilder()
            .add(PURPOSE_ID, assetId)
            .add(COMPANY_ID, arguments.getString(COMPANY_ID))
            .add(ORGANIZATION_ID, arguments.getString(ORGANIZATION_ID))
            .add(PURPOSE_CATEGORY_OF_PURPOSE, arguments.getString(PURPOSE_CATEGORY_OF_PURPOSE))
            .add(PURPOSE_NAME, arguments.getString(PURPOSE_NAME))
            .add(PURPOSE_DESCRIPTION, arguments.getString(PURPOSE_DESCRIPTION))
            .add(PURPOSE_LEGAL_TEXT, arguments.getString(PURPOSE_LEGAL_TEXT))
            .add(PURPOSE_USER_FRIENDLY_TEXT, arguments.getString(PURPOSE_USER_FRIENDLY_TEXT))
            .add(PURPOSE_GUIDANCE, arguments.getString(PURPOSE_GUIDANCE))
            .add(PURPOSE_NOTE, arguments.getString(PURPOSE_NOTE))
            .build();
    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, createdAt)
        .build();
  }

  private JsonObject prepareArgument() {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
        .add(PURPOSE_CATEGORY_OF_PURPOSE, MOCKED_CATEGORY_OF_PURPOSE)
        .add(PURPOSE_NAME, MOCKED_PURPOSE_NAME)
        .add(PURPOSE_DESCRIPTION, MOCKED_PURPOSE_DESCRIPTION)
        .add(PURPOSE_LEGAL_TEXT, MOCKED_LEGAL_TEXT)
        .add(PURPOSE_USER_FRIENDLY_TEXT, MOCKED_USER_FRIENDLY_TEXT)
        .add(PURPOSE_GUIDANCE, MOCKED_GUIDANCE)
        .add(PURPOSE_NOTE, MOCKED_NOTE)
        .add(CREATED_AT, MOCKED_CREATED_AT)
        .build();
  }

  JsonObject prepareProperties() {
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

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }

  private JsonObject prepareUserProfile(String role) {
    JsonArray roles = Json.createArrayBuilder().add(role).build();
    return Json.createObjectBuilder()
        .add(ORGANIZATION_IDS, mockedOrganizationIds)
        .add(ROLES, roles)
        .build();
  }

  private JsonObject preparePermissionValidationArgument(
      JsonObject userProfile, JsonObject arguments) {
    JsonArray ROLES = Json.createArrayBuilder().add(ROLE_PROCESSOR).add(ROLE_CONTROLLER).build();
    return Json.createObjectBuilder()
        .add(ROLES_REQUIRED, ROLES)
        .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
        .add(ORGANIZATION_IDS_REQUIRED, userProfile.getJsonArray(ORGANIZATION_IDS))
        .add(
            ORGANIZATION_IDS_ARGUMENT,
            Json.createArrayBuilder().add(arguments.getString(ORGANIZATION_ID)).build())
        .build();
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_COMPANY_ID).build();
  }
}
