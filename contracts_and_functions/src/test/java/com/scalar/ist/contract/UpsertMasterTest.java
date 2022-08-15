package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_SCHEMA;
import static com.scalar.ist.common.Constants.ASSET_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.RECORD_MODE_UPDATE;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
import static com.scalar.ist.common.Constants.TABLE_SCHEMA;
import static com.scalar.ist.common.Constants.TABLE_SCHEMA_IS_MISSING;
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

import com.scalar.dl.ledger.crypto.CertificateEntry.Key;
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

public class UpsertMasterTest {
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_ORGANIZATION_ID = "org1";
  private static final String MOCKED_HOLDER_ID = "holder_1";
  private static final JsonArray MOCKED_ARRAY_STRING_ROLES =
      Json.createArrayBuilder().add(ROLE_CONTROLLER).add(ROLE_PROCESSOR).build();
  private final JsonArray mockedOrganizationIds =
      Json.createArrayBuilder()
          .add(UUID.randomUUID().toString())
          .add(MOCKED_ORGANIZATION_ID)
          .build();
  @Mock private Ledger ledger;
  private Key certificateKey = new Key(MOCKED_HOLDER_ID, 1);
  private UpsertMaster upsertMaster;

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    upsertMaster = spy(new UpsertMaster());
  }

  @Test
  public void invoke_RegisterBenefit_ShouldPass() {
    // Arrange
    JsonObject argument = Util.readArgumentsSampleFromResources("register_benefit.json");
    JsonObject properties = prepareProperties(false);
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile();
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(userProfile, argument);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    JsonObject putAssetRecordArgument = preparePutRecordArgument(argument, properties, false);
    when(upsertMaster.getCertificateKey()).thenReturn(certificateKey);
    doReturn(null)
        .when(upsertMaster)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(userProfile)
        .when(upsertMaster)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(upsertMaster)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(upsertMaster)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putAssetRecordArgument);

    // Act
    upsertMaster.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(upsertMaster).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(upsertMaster).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertMaster)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(upsertMaster).invokeSubContract(PUT_ASSET_RECORD, ledger, putAssetRecordArgument);
    verify(upsertMaster, times(4)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_UpdateBenefit_ShouldPass() {
    // Arrange
    JsonObject argument = Util.readArgumentsSampleFromResources("update_benefit.json");
    JsonObject properties = prepareProperties(true);
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile();
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(userProfile, argument);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    JsonObject getAssetRecordArgument = prepareGetAssetRecordArgument();
    JsonObject putAssetRecordArgument = preparePutRecordArgument(argument, properties, true);
    when(upsertMaster.getCertificateKey()).thenReturn(certificateKey);
    doReturn(null)
        .when(upsertMaster)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(userProfile)
        .when(upsertMaster)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(upsertMaster)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(JsonObject.EMPTY_JSON_OBJECT)
        .when(upsertMaster)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putAssetRecordArgument);
    doReturn(Util.readAssetSampleFromResources("benefit.json"))
        .when(upsertMaster)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    // Act
    upsertMaster.invoke(ledger, argument, Optional.of(properties));

    // Assert
    verify(upsertMaster).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(upsertMaster).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertMaster)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(upsertMaster).invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(upsertMaster).invokeSubContract(PUT_ASSET_RECORD, ledger, putAssetRecordArgument);
    verify(upsertMaster, times(5)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutAssetName_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = Util.readArgumentsSampleFromResources("update_benefit.json");
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA,
                        Util.readJsonSchemaFromResources("update_benefit.json"))
                    .build())
            .add(HOLDER_ID, MOCKED_HOLDER_ID)
            .add(ASSET_SCHEMA, Util.readAssetSchemaFromResources("benefit.json"))
            .add(TABLE_SCHEMA, Util.readTableSchemaFromResources("benefit.json"))
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              upsertMaster.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_NAME_IS_MISSING);
    verify(upsertMaster, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutHolderId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = Util.readArgumentsSampleFromResources("update_benefit.json");
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA,
                        Util.readJsonSchemaFromResources("update_benefit.json"))
                    .build())
            .add(ASSET_NAME, "benefit")
            .add(ASSET_SCHEMA, Util.readAssetSchemaFromResources("benefit.json"))
            .add(TABLE_SCHEMA, Util.readTableSchemaFromResources("benefit.json"))
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              upsertMaster.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(HOLDER_ID_IS_MISSING);
    verify(upsertMaster, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutAssetSchema_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = Util.readArgumentsSampleFromResources("update_benefit.json");
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA,
                        Util.readJsonSchemaFromResources("update_benefit.json"))
                    .build())
            .add(HOLDER_ID, MOCKED_HOLDER_ID)
            .add(ASSET_NAME, "benefit")
            .add(TABLE_SCHEMA, Util.readTableSchemaFromResources("benefit.json"))
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              upsertMaster.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(ASSET_SCHEMA_IS_MISSING);
    verify(upsertMaster, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutTableSchema_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = Util.readArgumentsSampleFromResources("update_benefit.json");
    JsonObject properties =
        Json.createObjectBuilder()
            .add(
                CONTRACT_ARGUMENT_SCHEMA,
                Json.createObjectBuilder()
                    .add(
                        VALIDATE_ARGUMENT_SCHEMA,
                        Util.readJsonSchemaFromResources("update_benefit.json"))
                    .build())
            .add(HOLDER_ID, MOCKED_HOLDER_ID)
            .add(ASSET_NAME, "benefit")
            .add(ASSET_SCHEMA, Util.readAssetSchemaFromResources("benefit.json"))
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              upsertMaster.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(TABLE_SCHEMA_IS_MISSING);
    verify(upsertMaster, never()).invokeSubContract(any(), any(), any());
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_COMPANY_ID).build();
  }

  private JsonObject prepareProperties(boolean isUpdate) {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(
                    VALIDATE_ARGUMENT_SCHEMA,
                    isUpdate
                        ? Util.readJsonSchemaFromResources("update_benefit.json")
                        : Util.readJsonSchemaFromResources("register_benefit.json"))
                .build())
        .add(HOLDER_ID, MOCKED_HOLDER_ID)
        .add(ASSET_NAME, "benefit")
        .add(ASSET_SCHEMA, Util.readAssetSchemaFromResources("benefit.json"))
        .add(TABLE_SCHEMA, Util.readTableSchemaFromResources("benefit.json"))
        .build();
  }

  private JsonObject prepareUserProfile() {
    return Json.createObjectBuilder()
        .add(ORGANIZATION_IDS, mockedOrganizationIds)
        .add(ROLES, MOCKED_ARRAY_STRING_ROLES)
        .build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
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

  private JsonObject preparePutRecordArgument(
      JsonObject argument, JsonObject properties, boolean isUpdate) {
    String assetId = prepareAssetId(properties, argument);
    JsonObject recordData =
        isUpdate
            ? Util.readAssetSampleFromResources("updated_benefit.json")
            : Util.readAssetSampleFromResources("benefit.json");
    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, recordData)
        .add(RECORD_MODE, isUpdate ? RECORD_MODE_UPDATE : RECORD_MODE_INSERT)
        .add(RECORD_IS_HASHED, false)
        .add(
            CREATED_AT,
            isUpdate ? argument.getJsonNumber(UPDATED_AT) : argument.getJsonNumber(CREATED_AT))
        .build();
  }

  private String prepareAssetId(JsonObject properties, JsonObject argument) {
    String assetName = properties.getString(ASSET_NAME) + properties.getString(ASSET_VERSION, "");
    return String.join(
        "-",
        assetName,
        argument.getString(ORGANIZATION_ID),
        argument.getJsonNumber(CREATED_AT).toString());
  }

  private JsonObject prepareGetAssetRecordArgument() {
    return Json.createObjectBuilder()
        .add(ASSET_ID, Util.readAssetSampleFromResources("benefit.json").getString("benefit_id"))
        .add(RECORD_IS_HASHED, false)
        .build();
  }
}
