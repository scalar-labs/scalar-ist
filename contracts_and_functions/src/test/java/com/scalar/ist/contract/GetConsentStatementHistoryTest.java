package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_VERSION;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.PERMITTED_ASSET_NAMES;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.RECORD_MODE;
import static com.scalar.ist.common.Constants.RECORD_MODE_SCAN;
import static com.scalar.ist.common.Constants.RECORD_VERSIONS;
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
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GetConsentStatementHistoryTest {
  private static final String SCHEMA_FILENAME = "get_consent_statement_history.json";
  private static final String MOCKED_ASSET_ID =
      "cs" + UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
  private static final String MOCKED_SALT = "mocked_salt";
  private static final String MOCKED_SCHEMA = "mocked_schema";
  private static final String MOCKED_COMPANY_ID = "scalar-labs";
  @Mock private Ledger ledger;
  private GetConsentStatementHistory getConsentStatementHistory;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    getConsentStatementHistory = spy(new GetConsentStatementHistory());
  }

  private JsonObject prepareArgument() {
    return Json.createObjectBuilder()
        .add(ASSET_ID, MOCKED_ASSET_ID)
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(RECORD_IS_HASHED, false)
        .build();
  }

  private JsonObject prepareProperties() {
    JsonArray permittedAssetNames =
        Json.createArrayBuilder().add("cs").build();
    return Json.createObjectBuilder()
        .add(PERMITTED_ASSET_NAMES, permittedAssetNames)
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                .build())
        .build();
  }

  private JsonObject prepareConsentStatement(String company, String version) {
    return Json.createObjectBuilder()
        .add(CONSENT_STATEMENT_ID, MOCKED_ASSET_ID)
        .add(COMPANY_ID, company)
        .add(CONSENT_STATEMENT_VERSION, version)
        .build();
  }

  private JsonObject prepareConsentStatementAssetDataList() {
    return Json.createObjectBuilder()
        .add(
            RECORD_VERSIONS,
            Json.createArrayBuilder()
                .add(prepareConsentStatement(MOCKED_COMPANY_ID, "1"))
                .add(prepareConsentStatement(MOCKED_COMPANY_ID, "2"))
                .add(prepareConsentStatement(MOCKED_COMPANY_ID, "3"))
                .add(prepareConsentStatement("indetail", "4"))
                .add(prepareConsentStatement(MOCKED_COMPANY_ID, "5"))
                .add(prepareConsentStatement("indetail", "6")))
        .build();
  }

  private JsonObject prepareGetAssetRecordArgument() {
    return Json.createObjectBuilder()
        .add(ASSET_ID, MOCKED_ASSET_ID)
        .add(RECORD_IS_HASHED, false)
        .add(RECORD_MODE, RECORD_MODE_SCAN)
        .build();
  }

  @Test
  public void invoke_ProperArgumentsAndPropertiesGiven_ShouldReturnJsonObject() {
    // arrange
    JsonObject argument = prepareArgument();
    JsonObject properties = prepareProperties();
    JsonObject userProfileArgument = prepareUserProfileArgument();
    JsonObject userProfile = prepareUserProfile(ROLE_ADMINISTRATOR);
    JsonObject permissionValidationArgument = preparePermissionValidationArgument(userProfile);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    JsonObject getAssetRecordArgument = prepareGetAssetRecordArgument();
    JsonObject getAssetRecord = prepareConsentStatementAssetDataList();
    doReturn(null)
        .when(getConsentStatementHistory)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(userProfile)
        .when(getConsentStatementHistory)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    doReturn(null)
        .when(getConsentStatementHistory)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    doReturn(getAssetRecord)
        .when(getConsentStatementHistory)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);

    // act
    JsonObject consentStatementHistory =
        getConsentStatementHistory.invoke(ledger, argument, Optional.of(properties));

    // assert
    consentStatementHistory
        .getJsonArray(RECORD_VERSIONS)
        .getValuesAs(JsonObject.class)
        .forEach(j -> assertThat(j.getString(COMPANY_ID).equals(MOCKED_COMPANY_ID)));
    verify(getConsentStatementHistory)
        .invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
    verify(getConsentStatementHistory)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(getConsentStatementHistory)
        .invokeSubContract(VALIDATE_PERMISSION, ledger, permissionValidationArgument);
    verify(getConsentStatementHistory)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(getConsentStatementHistory, times(4)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutSchema_ShouldThrowContractContextException() {
    // Arrange
    JsonArray permittedAssetNames =
        Json.createArrayBuilder().add("cs").build();
    JsonObject properties = Json.createObjectBuilder()
        .add(PERMITTED_ASSET_NAMES, permittedAssetNames)
        .build();
    JsonObject argument = prepareArgument();

    // Act
    // Assert
    assertThatThrownBy(
        () -> {
          getConsentStatementHistory.invoke(ledger, argument, Optional.of(properties));
        })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(getConsentStatementHistory, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_PropertyWithoutPermittedAssetNames_ShouldThrowContractContextException() {
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
          getConsentStatementHistory.invoke(ledger, argument, Optional.of(properties));
        })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    verify(getConsentStatementHistory, never()).invokeSubContract(any(), any(), any());
  }

  private JsonObject preparePermissionValidationArgument(JsonObject userProfile) {
    JsonArray ROLES =
        Json.createArrayBuilder()
            .add(ROLE_ADMINISTRATOR)
            .add(ROLE_PROCESSOR)
            .add(ROLE_CONTROLLER)
            .build();

    return Json.createObjectBuilder()
        .add(ROLES_REQUIRED, ROLES)
        .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
        .build();
  }

  private JsonObject prepareUserProfile(String role) {
    JsonArray roles = Json.createArrayBuilder().add(role).build();
    return Json.createObjectBuilder().add(ROLES, roles).build();
  }

  private JsonObject prepareUserProfileArgument() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_COMPANY_ID).build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }
}
