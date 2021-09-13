package com.scalar.ist.contract;

import com.scalar.dl.ledger.crypto.CertificateEntry;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.Optional;
import java.util.UUID;

import static com.scalar.ist.common.Constants.ASSET_ID;
import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.DISALLOWED_CONTRACT_EXECUTION_ORDER;
import static com.scalar.ist.common.Constants.GET_ASSET_RECORD;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.RECORD_IS_HASHED;
import static com.scalar.ist.common.Constants.REQUIRED_ATTRIBUTES_FOR_USER_PROFILE_ARE_MISSING;
import static com.scalar.ist.common.Constants.REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING;
import static com.scalar.ist.common.Constants.ROLES;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetUserProfileTest {
  private static final String SCHEMA_FILENAME = "get_user_profile.json";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_HOLDER_ID = UUID.randomUUID().toString();
  private static final String MOCKED_ASSET_NAME = "user_profile";
  private static final JsonArray mockedArrayAdminRoles =
      Json.createArrayBuilder().add(ROLE_ADMINISTRATOR).add(ROLE_PROCESSOR).build();
  private final JsonArray mockedOrganizationIds =
      Json.createArrayBuilder()
          .add(UUID.randomUUID().toString())
          .add(UUID.randomUUID().toString())
          .build();
  @Mock private Ledger ledger;
  @Mock private CertificateEntry.Key certificateKey;
  private GetUserProfile getUserProfile;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    getUserProfile = spy(new GetUserProfile());
  }

  @Test
  public void invoke_ProperArgumentGivenAndAssetExists_ShouldRetrieveProperUserProfile() {
    // arrange
    JsonObject argument = prepareArguments();
    JsonObject properties = prepareProperties();
    JsonObject getAssetRecordArgument = prepareAssetRecordArgument();
    JsonObject assetRecord = prepareAssetRecord();
    when(getUserProfile.getCertificateKey()).thenReturn(certificateKey);
    when(getUserProfile.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    doReturn(null)
        .when(getUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(assetRecord)
        .when(getUserProfile)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);

    // act
    JsonObject userProfile = getUserProfile.invoke(ledger, argument, Optional.of(properties));
    JsonObject mockedUserProfile =
        Json.createObjectBuilder()
            .add(ORGANIZATION_IDS, mockedOrganizationIds)
            .add(ROLES, mockedArrayAdminRoles)
            .build();

    // assert
    assertThat(userProfile).isEqualTo(mockedUserProfile);
    verify(getUserProfile).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(getUserProfile).invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(getUserProfile, times(2)).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_MissingUserProfile_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArguments();
    JsonObject properties = prepareProperties();
    JsonObject getAssetRecordArgument = prepareAssetRecordArgument();
    JsonObject assetRecordWithMissingOrganizationIds =
        Json.createObjectBuilder().add(ROLES, mockedArrayAdminRoles).build();
    JsonObject validateArgumentArgument = prepareValidationArgument(argument, properties);
    when(getUserProfile.getCertificateKey()).thenReturn(certificateKey);
    when(getUserProfile.getCertificateKey().getHolderId()).thenReturn(MOCKED_HOLDER_ID);
    doReturn(null)
        .when(getUserProfile)
        .invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    doReturn(assetRecordWithMissingOrganizationIds)
        .when(getUserProfile)
        .invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);

    // act
    // assert
    assertThatThrownBy(
            () -> {
              getUserProfile.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(REQUIRED_ATTRIBUTES_FOR_USER_PROFILE_ARE_MISSING);
    verify(getUserProfile).invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentArgument);
    verify(getUserProfile).invokeSubContract(GET_ASSET_RECORD, ledger, getAssetRecordArgument);
    verify(getUserProfile, times(2)).invokeSubContract(any(), any(), any());
    verify(ledger, never()).get(anyString());
  }

  @Test
  public void invoke_PropertyMissing_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArguments();

    // act
    // assert
    assertThatThrownBy(
            () -> {
              getUserProfile.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING);
    verify(ledger, never()).get(anyString());
  }

  @Test
  public void invoke_PropertyWithoutSchema_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArguments();
    JsonObject properties = Json.createObjectBuilder().add(HOLDER_ID, MOCKED_HOLDER_ID).build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              getUserProfile.invoke(ledger, argument, Optional.of(properties));
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    verify(getUserProfile, never()).invokeSubContract(any(), any(), any());
  }

  @Test
  public void invoke_WithRootUser_ShouldThrowContractContextException() {
    // arrange
    JsonObject argument = prepareArguments();
    when(getUserProfile.isRoot()).thenReturn(true);

    // act
    // assert
    assertThatThrownBy(
            () -> {
              getUserProfile.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(DISALLOWED_CONTRACT_EXECUTION_ORDER);
  }

  private JsonObject prepareArguments() {
    return Json.createObjectBuilder().add(COMPANY_ID, MOCKED_COMPANY_ID).build();
  }

  private JsonObject prepareProperties() {
    return Json.createObjectBuilder()
        .add(
            CONTRACT_ARGUMENT_SCHEMA,
            Json.createObjectBuilder()
                .add(VALIDATE_ARGUMENT_SCHEMA, Util.readJsonSchemaFromResources(SCHEMA_FILENAME))
                .build())
        .add(ASSET_NAME, MOCKED_ASSET_NAME)
        .build();
  }

  private JsonObject prepareAssetRecordArgument() {
    String assetId = String.join("-", MOCKED_ASSET_NAME, MOCKED_COMPANY_ID, MOCKED_HOLDER_ID);
    return Json.createObjectBuilder().add(ASSET_ID, assetId).add(RECORD_IS_HASHED, false).build();
  }

  private JsonObject prepareAssetRecord() {
    return Json.createObjectBuilder()
        .add(ORGANIZATION_IDS, mockedOrganizationIds)
        .add(ROLES, mockedArrayAdminRoles)
        .build();
  }

  private JsonObject prepareValidationArgument(JsonObject argument, JsonObject properties) {
    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argument)
        .add(VALIDATE_ARGUMENT_SCHEMA, properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA))
        .build();
  }
}
