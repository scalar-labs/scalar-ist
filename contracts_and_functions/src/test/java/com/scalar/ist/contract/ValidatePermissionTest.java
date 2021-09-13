package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.DISALLOWED_CONTRACT_EXECUTION_ORDER;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
import static com.scalar.ist.common.Constants.PERMISSION_DENIED;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.ROLE_ADMINISTRATOR;
import static com.scalar.ist.common.Constants.ROLE_CONTROLLER;
import static com.scalar.ist.common.Constants.ROLE_PROCESSOR;
import static com.scalar.ist.common.Constants.ROLE_SYSADMIN;
import static com.scalar.ist.common.Constants.ROLE_SYSOPERATOR;
import static com.scalar.ist.common.Constants.USER_PROFILE_ROLES;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import java.util.Optional;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ValidatePermissionTest {
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private final JsonArray mockedUserProfileArrayRoles =
      Json.createArrayBuilder().add(ROLE_CONTROLLER).build();
  private final JsonArray mockedControllerAndAdminRoles =
      Json.createArrayBuilder().add(ROLE_CONTROLLER).add(ROLE_PROCESSOR).build();
  private final JsonArray mockedRequiredArrayRoles =
      Json.createArrayBuilder()
          .add(ROLE_CONTROLLER)
          .add(ROLE_ADMINISTRATOR)
          .add(ROLE_SYSADMIN)
          .build();
  private final JsonArray mockedIncorrectArrayRoles =
      Json.createArrayBuilder().add(ROLE_SYSOPERATOR).build();
  private final JsonArray organizationIds =
      Json.createArrayBuilder()
          .add(MOCKED_ORGANIZATION_ID)
          .add(UUID.randomUUID().toString())
          .build();
  @Mock Ledger ledger;
  private ValidatePermission validatePermission;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    validatePermission = spy(new ValidatePermission());
  }

  @Test
  public void invoke_ProperArguments_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();

    // Act
    validatePermission.invoke(ledger, argument, Optional.empty());
  }

  @Test
  public void invoke_WithOneOfTheRoles_ShouldPass() {
    // Arrange
    JsonObject argument =
        Json.createObjectBuilder()
            .add(USER_PROFILE_ROLES, mockedControllerAndAdminRoles)
            .add(ROLES_REQUIRED, mockedRequiredArrayRoles)
            .build();

    // Act
    validatePermission.invoke(ledger, argument, Optional.empty());
  }

  @Test
  public void invoke_WithRoot_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument =
        Json.createObjectBuilder()
            .add(USER_PROFILE_ROLES, mockedControllerAndAdminRoles)
            .add(ROLES_REQUIRED, mockedRequiredArrayRoles)
            .build();
    when(validatePermission.isRoot()).thenReturn(true);

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              validatePermission.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessageContainingAll(DISALLOWED_CONTRACT_EXECUTION_ORDER);
  }

  @Test
  public void invoke_WithMissingRoles_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument =
        Json.createObjectBuilder()
            .add(USER_PROFILE_ROLES, mockedIncorrectArrayRoles)
            .add(ROLES_REQUIRED, mockedRequiredArrayRoles)
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              validatePermission.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessageContainingAll(PERMISSION_DENIED);
  }

  @Test
  public void invoke_WithInvalidOrganizationId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument =
        Json.createObjectBuilder()
            .add(USER_PROFILE_ROLES, mockedUserProfileArrayRoles)
            .add(ROLES_REQUIRED, mockedRequiredArrayRoles)
            .add(ORGANIZATION_IDS_REQUIRED, organizationIds)
            .add(
                ORGANIZATION_IDS_ARGUMENT,
                Json.createArrayBuilder().add(UUID.randomUUID().toString()).build())
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              validatePermission.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessageContainingAll(PERMISSION_DENIED);
  }

  private JsonObject prepareArgument() {
    return Json.createObjectBuilder()
        .add(USER_PROFILE_ROLES, mockedUserProfileArrayRoles)
        .add(ROLES_REQUIRED, mockedRequiredArrayRoles)
        .build();
  }
}
