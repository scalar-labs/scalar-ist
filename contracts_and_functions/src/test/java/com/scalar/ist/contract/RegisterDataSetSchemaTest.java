package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.DATA_CATEGORY;
import static com.scalar.ist.common.Constants.DATA_CLASSIFICATION;
import static com.scalar.ist.common.Constants.DATA_LOCATION;
import static com.scalar.ist.common.Constants.DATA_SET_DESCRIPTION;
import static com.scalar.ist.common.Constants.DATA_SET_NAME;
import static com.scalar.ist.common.Constants.DATA_SET_SCHEMA;
import static com.scalar.ist.common.Constants.DATA_SET_SCHEMA_CHANGES;
import static com.scalar.ist.common.Constants.DATA_SET_SCHEMA_ID;
import static com.scalar.ist.common.Constants.DATA_TYPE;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_NOT_MATCHED;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
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

import com.scalar.dl.ledger.crypto.CertificateEntry;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import com.scalar.ist.util.Util;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RegisterDataSetSchemaTest {
  private static final String SCHEMA_FILENAME = "register_data_set_schema.json";
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final JsonArray MOCKED_DATA_TYPE =
      Json.createArrayBuilder().add("cookie data").add("gps data").build();
  private static final String MOCKED_DATA_SET_NAME = "mocked_data_set_name";
  private static final String MOCKED_DATA_SET_DESCRIPTION = "mocked_data_set_description";
  private static final JsonArray MOCKED_DATA_CATEGORY =
      Json.createArrayBuilder().add("PII").add("Identify").build();
  private static final JsonArray MOCKED_DATA_CLASSIFICATION =
      Json.createArrayBuilder().add("mocked_classification").build();
  private static final String MOCKED_DATA_SET_CHANGES = "";
  private static final long MOCKED_TIMESTAMP = 1L;
  private static final JsonArray MOCKED_ARRAY_STRING_ROLES =
      Json.createArrayBuilder().add(ROLE_CONTROLLER).add(ROLE_PROCESSOR).build();
  private static final String MOCKED_ASSET_NAME = "data_set_schema";
  private final String MOCKED_DATA_LOCATION = createDataLocation().toString();
  private final JsonArray organizationIds =
      Json.createArrayBuilder()
          .add(MOCKED_ORGANIZATION_ID)
          .add(UUID.randomUUID().toString())
          .build();
  @Mock Ledger ledger;
  @Mock CertificateEntry.Key key;
  private RegisterDataSetSchema registerDataSetSchema;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    registerDataSetSchema = spy(new RegisterDataSetSchema());
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
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(registerDataSetSchema.getCertificateKey()).thenReturn(key);
    when(key.getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(userProfile)
        .when(registerDataSetSchema)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(registerDataSetSchema)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    doReturn(null)
        .when(registerDataSetSchema)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(null)
        .when(registerDataSetSchema)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    registerDataSetSchema.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(registerDataSetSchema).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(registerDataSetSchema).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordArgument);
    verify(registerDataSetSchema)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(registerDataSetSchema)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(registerDataSetSchema, times(4)).invokeSubContract(any(), any(), any());
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
              registerDataSetSchema.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(registerDataSetSchema, never()).invokeSubContract(any(), any(), any());
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
              registerDataSetSchema.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(registerDataSetSchema, never()).invokeSubContract(any(), any(), any());
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
          registerDataSetSchema.invoke(ledger, argument, Optional.of(properties));
        })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NAME_IS_MISSING);
    verify(registerDataSetSchema, never()).invokeSubContract(any(), any(), any());
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
    JsonObject validateArgumentArgument =
        prepareValidationArgument(argument, propertiesWithWrongHolderId);
    when(registerDataSetSchema.getCertificateKey()).thenReturn(key);
    when(registerDataSetSchema.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(null)
        .when(registerDataSetSchema)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // act
    // assert
    assertThatThrownBy(
            () -> {
              registerDataSetSchema.invoke(
                  ledger, argument, Optional.of(propertiesWithWrongHolderId));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_NOT_MATCHED);
    verify(registerDataSetSchema)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(registerDataSetSchema).invokeSubContract(any(), any(), any());
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
        .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
        .add(DATA_SET_NAME, MOCKED_DATA_SET_NAME)
        .add(DATA_SET_DESCRIPTION, MOCKED_DATA_SET_DESCRIPTION)
        .add(DATA_LOCATION, MOCKED_DATA_LOCATION)
        .add(DATA_CATEGORY, MOCKED_DATA_CATEGORY)
        .add(DATA_CLASSIFICATION, MOCKED_DATA_CLASSIFICATION)
        .add(DATA_SET_SCHEMA_CHANGES, MOCKED_DATA_SET_CHANGES)
        .add(DATA_TYPE, MOCKED_DATA_TYPE)
        .add(DATA_SET_SCHEMA, JsonValue.EMPTY_JSON_OBJECT)
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
    JsonNumber createdAt = argument.getJsonNumber(CREATED_AT);
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    String assetId =
        String.join("-", assetName, argument.getString(ORGANIZATION_ID), createdAt.toString());

    JsonObject data =
        Json.createObjectBuilder()
            .add(DATA_SET_SCHEMA_ID, assetId)
            .add(COMPANY_ID, argument.getString(COMPANY_ID))
            .add(ORGANIZATION_ID, argument.getString(ORGANIZATION_ID))
            .add(DATA_SET_NAME, argument.getString(DATA_SET_NAME))
            .add(DATA_SET_DESCRIPTION, argument.getString(DATA_SET_DESCRIPTION))
            .add(DATA_CATEGORY, argument.getJsonArray(DATA_CATEGORY))
            .add(DATA_TYPE, argument.getJsonArray(DATA_TYPE))
            .add(DATA_CLASSIFICATION, argument.getJsonArray(DATA_CLASSIFICATION))
            .add(DATA_SET_SCHEMA, argument.getJsonObject(DATA_SET_SCHEMA))
            .add(DATA_SET_SCHEMA_CHANGES, argument.getString(DATA_SET_SCHEMA_CHANGES))
            .build();

    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, data)
        .add(RECORD_IS_HASHED, false)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .add(CREATED_AT, createdAt)
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

  private JsonObject createDataLocation() {
    return Json.createObjectBuilder()
        .add("uri", "https://example.com/pds/uuid")
        .add("connect", "access_token")
        .add("authenticate", "oauth server")
        .build();
  }
}
