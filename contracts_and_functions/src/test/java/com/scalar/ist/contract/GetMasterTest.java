package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.PERMITTED_ASSET_NAMES;
import static com.scalar.ist.common.Constants.PERMITTED_ASSET_NAMES_IS_MISSING;
import static com.scalar.ist.common.Constants.REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
import static com.scalar.ist.common.Constants.USER_PROFILE_ROLES;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.VALIDATE_PERMISSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import com.scalar.ist.util.Util;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GetMasterTest {

  private static final String SCHEMA_FILENAME = "get_master.json";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_ASSET_ID = "pp";
  private static final JsonArray MOCKED_PERMITTED_ASSET_NAMES = Json.createArrayBuilder()
      .add(MOCKED_ASSET_ID)
      .build();
  private static final JsonArray mockedRolesArray =
      Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).add(ROLE_PROCESSOR).add(ROLE_CONTROLLER)
          .build();
  @Mock
  private Ledger ledger;
  @Mock
  private GetMaster getMaster;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    getMaster = spy(new GetMaster());
  }

  @Test
  public void invoke_ProperArgumentGivenAndAssetExists_ShouldRetrieveProperUserProfile() {
    // arrange
    JsonObject argument = prepareArguments();
    JsonObject properties = prepareProperties();
    JsonObject getAssetRecordArgument = prepareAssetRecordArgument();
    JsonObject assetRecord = prepareAssetRecord();
    JsonObject userProfile = prepareUserProfile(ROLE_ADMINISTRATOR);
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject permissionValidationArgument = preparePermissionValidationArgument(userProfile);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(getMaster)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(assetRecord)
        .when(getMaster)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    doReturn(userProfile)
        .when(getMaster)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(getMaster)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);

    // act
    JsonObject asset = getMaster.invoke(ledger, argument, Optional.of(properties));
    JsonObject mockedAssetData = Json.createObjectBuilder().add(COMPANY_ID, MOCKED_COMPANY_ID)
        .build();

    // assert
    assertThat(asset).isEqualTo(mockedAssetData);
    verify(getMaster).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(getMaster, times(4)).invokeSubContract(any(), any(), any());
  }


  @Test
  public void invoke_PropertyMissing_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArguments();

    // act
    // assert
    assertThatThrownBy(
        () -> {
          getMaster.invoke(ledger, argument, Optional.empty());
        })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    verify(ledger, never()).get(anyString());
  }

  @Test
  public void invoke_PropertyMissingPermittedNames_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArguments();
    JsonObject propertiesMissingPermittedNames = Json.createObjectBuilder()
        .build();

    // act
    // assert
    assertThatThrownBy(
        () -> {
          getMaster.invoke(ledger, argument, Optional.of(propertiesMissingPermittedNames));
        })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(PERMITTED_ASSET_NAMES_IS_MISSING);
    verify(ledger, never()).get(anyString());
  }

  @Test
  public void invoke_PropertyWithoutSchema_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArguments();
    JsonObject properties = Json.createObjectBuilder()
        .add(PERMITTED_ASSET_NAMES, MOCKED_PERMITTED_ASSET_NAMES).build();

    // Act
    // Assert
    assertThatThrownBy(
        () -> {
          getMaster.invoke(ledger, argument, Optional.of(properties));
        })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(getMaster, never()).invokeSubContract(any(), any(), any());
  }

  private JsonObject prepareArguments() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(ASSET_ID, MOCKED_ASSET_ID)
        .build();
  }

  private JsonObject prepareUserProfile(String role) {
    JsonArray roles = Json.createArrayBuilder().add(role).build();
    return Json.createObjectBuilder()
        .add(ROLES, roles)
        .build();
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .build();
  }

  private JsonObject preparePermissionValidationArgument(JsonObject userProfile) {
    return Json.createObjectBuilder()
        .add(ROLES_REQUIRED, mockedRolesArray)
        .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
        .build();
  }

  private JsonObject prepareProperties() {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                .build())
        .add(PERMITTED_ASSET_NAMES, MOCKED_PERMITTED_ASSET_NAMES)
        .build();
  }

  private JsonObject prepareAssetRecordArgument() {
    return Json.createObjectBuilder().add(ASSET_ID, MOCKED_ASSET_ID).build();
  }

  private JsonObject prepareAssetRecord() {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }
}
