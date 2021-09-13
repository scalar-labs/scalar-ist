package com.scalar.ist.contract;

import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.util.Hasher;
import com.scalar.ist.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_ABSTRACT;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_PURPOSE_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_THIRD_PARTY_IDS;
import static com.scalar.ist.common.Constants.CONSENT_STATEMENT_VERSION;
import static com.scalar.ist.common.Constants.CREATED_AT;
import static com.scalar.ist.common.Constants.DISALLOWED_CONTRACT_EXECUTION_ORDER;
import static com.scalar.ist.common.Constants.INVALID_CONTRACT_ARGUMENTS;
import static com.scalar.ist.common.Constants.INVALID_CONTRACT_ARGUMENTS_SCHEMA;
import static com.scalar.ist.common.Constants.ORGANIZATION_ID;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ValidateArgumentTest {
  private static final String SCHEMA_FILENAME = "validate_argument_test_schema.json";
  private static final String MOCKED_COMPANY_ID = "scalar-labs.com";
  private static final String MOCKED_ORGANIZATION_ID = UUID.randomUUID().toString();
  private static final String MOCKED_ABSTRACT = "Abstract of ConsentStatement";
  private static final String MOCKED_VERSION = "20191211";
  private static final String MOCKED_CONSENT_STATEMENT = "Content of consent statement";
  private static final JsonNumber MOCKED_CREATED_AT = Json.createValue(Long.MAX_VALUE);
  private final JsonArray mockedDatasetSchemaIds = createHashedIdsArray();
  private final JsonArray mockedPurposeIds = createHashedIdsArray();
  private final JsonArray mockedThirdPartyIds = createHashedIdsArray();
  private final String mockedDataRetentionPolicyId = Hasher.hash(UUID.randomUUID().toString());
  @Mock Ledger ledger;
  private ValidateArgument validateArgument;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    validateArgument = spy(new ValidateArgument());
  }

  @Test
  public void invoke_ProperArgumentsGivenAndMatchSchema_ShouldPass() {
    // Arrange
    JsonObject argument = prepareArgument();

    // Act
    validateArgument.invoke(ledger, argument, Optional.empty());
  }

  @Test
  public void invoke_WithRoot_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argument = prepareArgument();
    when(validateArgument.isRoot()).thenReturn(true);

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              validateArgument.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(DISALLOWED_CONTRACT_EXECUTION_ORDER);
    verify(validateArgument).invoke(any(), any(), any());
  }

  @Test
  public void invoke_WithMissingCompanyId_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argumentWithMissingCompanyId =
        Json.createObjectBuilder()
            .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
            .add(CONSENT_STATEMENT_ABSTRACT, MOCKED_ABSTRACT)
            .add(CONSENT_STATEMENT_VERSION, MOCKED_VERSION)
            .add(CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS, mockedDatasetSchemaIds)
            .add(CONSENT_STATEMENT_PURPOSE_IDS, mockedPurposeIds)
            .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, mockedThirdPartyIds)
            .add(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID, mockedDataRetentionPolicyId)
            .add(CONSENT_STATEMENT, MOCKED_CONSENT_STATEMENT)
            .add(CREATED_AT, MOCKED_CREATED_AT)
            .build();
    JsonObject schema = prepareSchema();
    JsonObject argument =
        Json.createObjectBuilder()
            .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argumentWithMissingCompanyId)
            .add(VALIDATE_ARGUMENT_SCHEMA, schema)
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              validateArgument.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessageContainingAll(INVALID_CONTRACT_ARGUMENTS, COMPANY_ID);
    verify(validateArgument).invoke(any(), any(), any());
  }

  @Test
  public void invoke_WithWrongOrganizationIdFormat_ShouldThrowContractContextException() {
    // Arrange
    JsonObject argumentWithWrongOrganizationIdFormat =
        Json.createObjectBuilder()
            .add(COMPANY_ID, MOCKED_COMPANY_ID)
            .add(ORGANIZATION_ID, "@@")
            .add(CONSENT_STATEMENT_ABSTRACT, MOCKED_ABSTRACT)
            .add(CONSENT_STATEMENT_VERSION, MOCKED_VERSION)
            .add(CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS, mockedDatasetSchemaIds)
            .add(CONSENT_STATEMENT_PURPOSE_IDS, mockedPurposeIds)
            .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, mockedThirdPartyIds)
            .add(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID, mockedDataRetentionPolicyId)
            .add(CONSENT_STATEMENT, MOCKED_CONSENT_STATEMENT)
            .add(CREATED_AT, MOCKED_CREATED_AT)
            .build();
    JsonObject schema = prepareSchema();
    JsonObject argument =
        Json.createObjectBuilder()
            .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, argumentWithWrongOrganizationIdFormat)
            .add(VALIDATE_ARGUMENT_SCHEMA, schema)
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              validateArgument.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessageContainingAll(INVALID_CONTRACT_ARGUMENTS, ORGANIZATION_ID);
    verify(validateArgument).invoke(any(), any(), any());
  }

  @Test
  public void invoke_WithInvalidSchema_ShouldThrowContractContextException() {
    // Arrange
    JsonObject contractArgument = prepareContractArgument();
    String schema = "bad_schema";
    JsonObject argument =
        Json.createObjectBuilder()
            .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, contractArgument)
            .add(VALIDATE_ARGUMENT_SCHEMA, schema)
            .build();

    // Act
    // Assert
    assertThatThrownBy(
            () -> {
              validateArgument.invoke(ledger, argument, Optional.empty());
            })
        .isExactlyInstanceOf(ContractContextException.class)
        .hasMessage(INVALID_CONTRACT_ARGUMENTS_SCHEMA);
    verify(validateArgument).invoke(any(), any(), any());
  }

  private JsonObject prepareArgument() {
    JsonObject contractArgument = prepareContractArgument();
    JsonObject contractSchema = prepareSchema();

    return Json.createObjectBuilder()
        .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, contractArgument)
        .add(VALIDATE_ARGUMENT_SCHEMA, contractSchema)
        .build();
  }

  private JsonObject prepareContractArgument() {
    return Json.createObjectBuilder()
        .add(COMPANY_ID, MOCKED_COMPANY_ID)
        .add(ORGANIZATION_ID, MOCKED_ORGANIZATION_ID)
        .add(CONSENT_STATEMENT_ABSTRACT, MOCKED_ABSTRACT)
        .add(CONSENT_STATEMENT_VERSION, MOCKED_VERSION)
        .add(CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS, mockedDatasetSchemaIds)
        .add(CONSENT_STATEMENT_PURPOSE_IDS, mockedPurposeIds)
        .add(CONSENT_STATEMENT_THIRD_PARTY_IDS, mockedThirdPartyIds)
        .add(CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID, mockedDataRetentionPolicyId)
        .add(CONSENT_STATEMENT, MOCKED_CONSENT_STATEMENT)
        .add(CREATED_AT, MOCKED_CREATED_AT)
        .build();
  }

  private JsonObject prepareSchema() {
    return Json.createReader(
            Objects.requireNonNull(
                Util.class.getClassLoader().getResourceAsStream(SCHEMA_FILENAME)))
        .readObject();
  }

  private JsonArray createHashedIdsArray() {
    return Json.createArrayBuilder()
        .add(Hasher.hash(UUID.randomUUID().toString()))
        .add(Hasher.hash(UUID.randomUUID().toString()))
        .build();
  }
}
