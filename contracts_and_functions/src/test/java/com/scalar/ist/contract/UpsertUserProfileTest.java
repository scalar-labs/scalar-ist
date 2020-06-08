package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_VERSION;
import static com.scalar.ist.common.Constants.COMPANY_ASSET_NAME;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HASHED_ASSET_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.ORGANIZATIONS;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
import static com.scalar.ist.common.Constants.PERMISSION_DENIED;
import static com.scalar.ist.common.Constants.PUT_ASSET_RECORD;
import static com.scalar.ist.common.Constants.RECORD_DATA;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_INSERT;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
import static com.scalar.ist.common.Constants.ROLE_SYSADMIN;
import static com.scalar.ist.common.Constants.ROLE_SYSOPERATOR;
import static com.scalar.ist.common.Constants.UPDATED_AT;
import static com.scalar.ist.common.Constants.USER_PROFILE_ASSET_NAME;
import static com.scalar.ist.common.Constants.USER_PROFILE_ASSET_VERSION;
import static com.scalar.ist.common.Constants.USER_PROFILE_EXECUTOR_COMPANY_ID;
import static com.scalar.ist.common.Constants.USER_PROFILE_ROLES;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.VALIDATE_PERMISSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import com.scalar.ist.util.Util;
import java.io.StringReader;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpsertUserProfileTest {
  private static final String SCHEMA_FILENAME = "upsert_user_profile.json";
  private static final String MOCKED_USER_PROFILE_ASSET_NAME = "user_profile";
  private static final String MOCKED_COMPANY_ASSET_NAME = "company";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final String MOCKED_EXECUTOR_COMPANY_ID = "servicer.com";
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final String MOCKED_EXECUTOR_HOLDER_ID = UUID.randomUUID().toString();
  private static final String MOCKED_HASHED_ASSET_ID = UUID.randomUUID().toString();
  private static final JsonArray mockedArrayOpAdminRole =
      Json.createArrayBuilder().add(ROLE_SYSOPERATOR).build();
  private static final JsonArray mockedArrayProcessorRole =
      Json.createArrayBuilder().add(ROLE_PROCESSOR).build();
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(Long.MAX_VALUE);
  private final JsonArray mockedOrganizationIds =
      Json.createArrayBuilder().add(MOCKED_ORGANIZATION_ID).build();
  @Mock private Ledger ledger;
  private UpsertUserProfile upsertUserProfile;

  @BeforeEach
  private void setUp() {
    MockitoAnnotations.initMocks(this);
    upsertUserProfile = spy(new UpsertUserProfile());
  }

  @Test
  public void invoke_ProperArgumentsAndPropertiesGiven_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument(ROLE_ADMINISTRATOR, MOCKED_COMPANY_ID);
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile();
    JsonObject companyAssetArgument = prepareCompanyAssetArgument(argument);
    JsonObject companyAsset = prepareCompanyAsset();
    JsonObject putRecordsArgument = preparePutRecordArgument(argument, properties);
    JsonObject mockedUserProfileReturnedObject =
        Json.createObjectBuilder().add(HASHED_ASSET_ID, MOCKED_HASHED_ASSET_ID).build();
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(userProfile, argument, companyAsset);
    doReturn(userProfile)
        .when(upsertUserProfile)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(companyAsset)
        .when(upsertUserProfile)
        .invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    doReturn(null)
        .when(upsertUserProfile)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(mockedUserProfileReturnedObject)
        .when(upsertUserProfile)
        .invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordsArgument);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(upsertUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    JsonObject userProfileReturnedObject =
        upsertUserProfile.invoke(ledger, argument, Optional.of(properties));

    // Assert
    assertThat(userProfileReturnedObject).isEqualTo(mockedUserProfileReturnedObject);
    verify(upsertUserProfile).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(upsertUserProfile)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(upsertUserProfile).invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    verify(upsertUserProfile).invokeSubContract(PUT_ASSET_RECORD, ledger, putRecordsArgument);
    verify(upsertUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertUserProfile, times(5)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void
      invoke_WithoutSysAdminOrOpAdminOrAdminRoleToRegisterAdmin_ShouldThrowContractContextException() {
    JsonObject properties = prepareProperties();
    JsonObject argument = prepareArgument(ROLE_ADMINISTRATOR, MOCKED_EXECUTOR_COMPANY_ID);
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject companyAssetArgument = prepareCompanyAssetArgument(argument);
    JsonObject companyAsset = prepareCompanyAsset();
    JsonObject userProfileWithUnauthorizedRole =
        Json.createObjectBuilder()
            .add(COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID)
            .add(ORGANIZATION_IDS, mockedOrganizationIds)
            .add(ROLES, Json.createArrayBuilder().add(ROLE_CONTROLLER).build())
            .build();
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(
            userProfileWithUnauthorizedRole, argument, companyAsset);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(userProfileWithUnauthorizedRole)
        .when(upsertUserProfile)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(upsertUserProfile)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(companyAsset)
        .when(upsertUserProfile)
        .invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    doReturn(null)
        .when(upsertUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    Assertions.assertThatThrownBy(
            () -> {
              upsertUserProfile.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(PERMISSION_DENIED);
    verify(upsertUserProfile).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(upsertUserProfile).invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    verify(upsertUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertUserProfile)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(upsertUserProfile, times(4)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithoutSysAdminRoleToRegisterSysAdmin_ShouldThrowContractContextException() {
    JsonObject properties = prepareProperties();
    JsonObject argument = prepareArgument(ROLE_SYSADMIN, MOCKED_EXECUTOR_COMPANY_ID);
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject companyAssetArgument = prepareCompanyAssetArgument(argument);
    JsonObject companyAsset = prepareCompanyAsset();
    JsonObject userProfileWithUnauthorizedRole =
        Json.createObjectBuilder()
            .add(COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID)
            .add(ORGANIZATION_IDS, mockedOrganizationIds)
            .add(ROLES, Json.createArrayBuilder().add(ROLE_SYSOPERATOR).build())
            .build();
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(
            userProfileWithUnauthorizedRole, argument, companyAsset);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(userProfileWithUnauthorizedRole)
        .when(upsertUserProfile)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(upsertUserProfile)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(companyAsset)
        .when(upsertUserProfile)
        .invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    doReturn(null)
        .when(upsertUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    Assertions.assertThatThrownBy(
            () -> {
              upsertUserProfile.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(PERMISSION_DENIED);
    verify(upsertUserProfile).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(upsertUserProfile).invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    verify(upsertUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertUserProfile)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(upsertUserProfile, times(4)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void
      invoke_WithoutSysAdminOrOpAdminRoleToRegisterOpAdmin_ShouldThrowContractContextException() {
    JsonObject properties = prepareProperties();
    JsonObject argument = prepareArgument(ROLE_SYSOPERATOR, MOCKED_EXECUTOR_COMPANY_ID);
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject companyAssetArgument = prepareCompanyAssetArgument(argument);
    JsonObject companyAsset = prepareCompanyAsset();
    JsonObject userProfileWithUnauthorizedRole =
        Json.createObjectBuilder()
            .add(COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID)
            .add(ORGANIZATION_IDS, mockedOrganizationIds)
            .add(ROLES, Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build())
            .build();
    JsonObject permissionValidationArgument =
        preparePermissionValidationArgument(
            userProfileWithUnauthorizedRole, argument, companyAsset);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(userProfileWithUnauthorizedRole)
        .when(upsertUserProfile)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(upsertUserProfile)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(companyAsset)
        .when(upsertUserProfile)
        .invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    doReturn(null)
        .when(upsertUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    Assertions.assertThatThrownBy(
            () -> {
              upsertUserProfile.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(PERMISSION_DENIED);
    verify(upsertUserProfile).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(upsertUserProfile).invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    verify(upsertUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertUserProfile)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(upsertUserProfile, times(4)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void
      invoke_WithoutMatchingExecutorCompanyIdAndCompanyId_ShouldThrowContractContextException() {
    String UNMATCHED_COMPANY_ID = "otherServicer.com";
    JsonObject properties = prepareProperties();
    JsonObject argument = prepareArgument(ROLE_ADMINISTRATOR, MOCKED_COMPANY_ID);
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject companyAssetArgument = prepareCompanyAssetArgument(argument);
    JsonObject companyAsset = prepareCompanyAsset();
    JsonObject userProfile =
        Json.createObjectBuilder()
            .add(COMPANY_ID, UNMATCHED_COMPANY_ID)
            .add(ORGANIZATION_IDS, mockedOrganizationIds)
            .add(ROLES, Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).build())
            .build();
    doReturn(userProfile)
        .when(upsertUserProfile)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(companyAsset)
        .when(upsertUserProfile)
        .invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(upsertUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);

    // Act
    // Assert
    Assertions.assertThatThrownBy(
            () -> {
              upsertUserProfile.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID);
    verify(upsertUserProfile).invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(upsertUserProfile).invokeSubContract(GET_ASSET_RECORD, ledger, companyAssetArgument);
    verify(upsertUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(upsertUserProfile, times(3)).invokeSubContract(any(), any(), any());
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID).build();
  }

  private JsonObject prepareArgument(String role, String companyId) {
    JsonArray roles = Json.createArrayBuilder().add(role).build();
    return Json.createObjectBuilder()
        .add(COMPANY_ID, companyId)
        .add(USER_PROFILE_EXECUTOR_COMPANY_ID, MOCKED_EXECUTOR_COMPANY_ID)
        .add(ORGANIZATION_IDS, mockedOrganizationIds)
        .add(CREATED_AT, MOCKED_CREATED_AT)
        .add(HOLDER_ID, MOCKED_HOLDER_ID)
        .add(ROLES, roles)
        .add(RECORD_MODE, RECORD_MODE_INSERT)
        .build();
  }

  private JsonObject prepareProperties() {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                .build())
        .add(HOLDER_ID, MOCKED_EXECUTOR_HOLDER_ID)
        .add(COMPANY_ASSET_NAME, MOCKED_COMPANY_ASSET_NAME)
        .add(USER_PROFILE_ASSET_NAME, MOCKED_USER_PROFILE_ASSET_NAME)
        .build();
  }

  private JsonObject preparePutRecordArgument(JsonObject argument, JsonObject properties) {
    String assetName = properties.getString(USER_PROFILE_ASSET_NAME) + properties.getString(USER_PROFILE_ASSET_VERSION, "");
    String assetId =
        String.format(
            "%s-%s-%s", assetName, argument.getString(COMPANY_ID), argument.getString(HOLDER_ID));
    JsonObject recordData =
        Json.createObjectBuilder()
            .add(COMPANY_ID, argument.getString(COMPANY_ID))
            .add(ORGANIZATION_IDS, argument.getJsonArray(ORGANIZATION_IDS))
            .add(ROLES, argument.getJsonArray(ROLES))
            .add(HOLDER_ID, argument.getString(HOLDER_ID))
            .build();
    return Json.createObjectBuilder()
        .add(ASSET_ID, assetId)
        .add(RECORD_DATA, recordData)
        .add(RECORD_MODE, argument.getString(RECORD_MODE))
        .add(RECORD_IS_HASHED, false)
        .add(CREATED_AT, argument.getJsonNumber(CREATED_AT))
        .build();
  }

  private JsonObject prepareUserProfile() {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(ORGANIZATION_IDS, mockedOrganizationIds)
        .add(ROLES, mockedArrayOpAdminRole)
        .build();
  }

  private JsonObject prepareCompanyAssetArgument(JsonObject arguments) {
    String companyAssetId =
        String.format("%s-%s", MOCKED_COMPANY_ASSET_NAME, arguments.getString(COMPANY_ID));
    return Json.createObjectBuilder()
        .add(ASSET_ID, companyAssetId)
        .add(RECORD_IS_HASHED, false)
        .build();
  }

  private JsonObject preparePermissionValidationArgument(
      JsonObject userProfile, JsonObject arguments, JsonObject companyAsset) {
    JsonArray ROLES =
        Json.createArrayBuilder()
            .add(ROLE_ADMINISTRATOR)
            .add(ROLE_SYSOPERATOR)
            .add(ROLE_SYSADMIN)
            .build();
    JsonArrayBuilder organizationIds = Json.createArrayBuilder();
    JsonArray organizations = companyAsset.getJsonArray(ORGANIZATIONS);
    organizations.stream()
        .map(
            org ->
                Json.createReader(new StringReader(org.toString()))
                    .readObject()
                    .getString(ORGANIZATION_ID))
        .forEach(organizationIds::add);
    return Json.createObjectBuilder()
        .add(ROLES_REQUIRED, ROLES)
        .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
        .add(ORGANIZATION_IDS_REQUIRED, organizationIds.build())
        .add(ORGANIZATION_IDS_ARGUMENT, arguments.getJsonArray(ORGANIZATION_IDS))
        .build();
  }

  private JsonObject prepareCompanyAsset() {
    JsonObject organizationId1 =
        Json.createObjectBuilder().add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID).build();
    JsonArray organizationArray = Json.createArrayBuilder().add(organizationId1).build();
    return Json.createObjectBuilder().add(ORGANIZATIONS, organizationArray).build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }
}
