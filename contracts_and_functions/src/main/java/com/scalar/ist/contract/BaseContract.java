package com.scalar.ist.contract;

import static com.scalar.ist.common.Constants.ASSET_NAME;
import static com.scalar.ist.common.Constants.ASSET_NAME_IS_MISSING;
import static com.scalar.ist.common.Constants.COMPANY_ID;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.CONTRACT_ARGUMENT_SCHEMA_IS_MISSING;
import static com.scalar.ist.common.Constants.GET_USER_PROFILE;
import static com.scalar.ist.common.Constants.HOLDER_ID;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_MISSING;
import static com.scalar.ist.common.Constants.HOLDER_ID_IS_NOT_MATCHED;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_ARGUMENT;
import static com.scalar.ist.common.Constants.ORGANIZATION_IDS_REQUIRED;
import static com.scalar.ist.common.Constants.ROLES_REQUIRED;
import static com.scalar.ist.common.Constants.USER_PROFILE_ROLES;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_CONTRACT_ARGUMENT;
import static com.scalar.ist.common.Constants.VALIDATE_ARGUMENT_SCHEMA;
import static com.scalar.ist.common.Constants.VALIDATE_PERMISSION;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.ist.common.Constants;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public abstract class BaseContract extends Contract {

  protected void validateProperties(Optional<JsonObject> properties) {
    if (!properties.isPresent() || !properties.get().containsKey(CONTRACT_ARGUMENT_SCHEMA)) {
      throw new ContractContextException(CONTRACT_ARGUMENT_SCHEMA_IS_MISSING);
    }
    if (!properties.get().containsKey(HOLDER_ID)) {
      throw new ContractContextException(HOLDER_ID_IS_MISSING);
    }
    if (!properties.get().containsKey(ASSET_NAME)) {
      throw new ContractContextException(ASSET_NAME_IS_MISSING);
    }
  }

  protected void validateHolderId(JsonObject properties) {
    if (!properties.getString(HOLDER_ID).equals(getCertificateKey().getHolderId())) {
      throw new ContractContextException(HOLDER_ID_IS_NOT_MATCHED);
    }
  }

  protected void validateArguments(Ledger ledger, JsonObject arguments, JsonObject properties) {
    JsonObject schema = properties.getJsonObject(CONTRACT_ARGUMENT_SCHEMA);
    JsonObject validateArgumentJson =
        Json.createObjectBuilder()
            .add(VALIDATE_ARGUMENT_CONTRACT_ARGUMENT, arguments)
            .add(VALIDATE_ARGUMENT_SCHEMA, schema)
            .build();

    invokeSubContract(VALIDATE_ARGUMENT, ledger, validateArgumentJson);
  }

  /**
   * Get user profile using the specified company ID key from arguments.
   * Some contracts use COMPANY_ID, others use EXECUTOR_COMPANY_ID.
   */
  protected JsonObject getUserProfile(
      Ledger ledger, JsonObject arguments, JsonObject properties, String companyIdKey) {
    JsonObject userProfileArgument =
        Json.createObjectBuilder()
            .add(COMPANY_ID, arguments.getString(companyIdKey))
            .build();
    return invokeSubContract(GET_USER_PROFILE, ledger, userProfileArgument);
  }

  /** Convenience overload using COMPANY_ID as the default key. */
  protected JsonObject getUserProfile(Ledger ledger, JsonObject arguments, JsonObject properties) {
    return getUserProfile(ledger, arguments, properties, COMPANY_ID);
  }

  /**
   * Validate user permissions with role and organization scope.
   * organizationIds: the user's organization IDs from their profile
   * targetOrganizationIds: the organization IDs being accessed in this operation
   */
  protected void validateUserPermissions(
      Ledger ledger,
      JsonObject userProfile,
      JsonObject arguments,
      JsonArray requiredRoles,
      JsonArray targetOrganizationIds) {
    JsonObject validateUserPermissionsArgument =
        Json.createObjectBuilder()
            .add(ROLES_REQUIRED, requiredRoles)
            .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
            .add(ORGANIZATION_IDS_REQUIRED, userProfile.getJsonArray(ORGANIZATION_IDS))
            .add(ORGANIZATION_IDS_ARGUMENT, targetOrganizationIds)
            .build();

    invokeSubContract(VALIDATE_PERMISSION, ledger, validateUserPermissionsArgument);
  }

  /** Convenience overload without organization scope (for system-level operations). */
  protected void validateUserPermissions(
      Ledger ledger,
      JsonObject userProfile,
      JsonObject arguments,
      JsonArray requiredRoles) {
    JsonObject validateUserPermissionsArgument =
        Json.createObjectBuilder()
            .add(ROLES_REQUIRED, requiredRoles)
            .add(USER_PROFILE_ROLES, userProfile.getJsonArray(Constants.ROLES))
            .build();

    invokeSubContract(VALIDATE_PERMISSION, ledger, validateUserPermissionsArgument);
  }

  @VisibleForTesting
  protected JsonObject invokeSubContract(
      String contractId, Ledger ledger, JsonObject arguments) {
    return invoke(contractId, ledger, arguments);
  }
}
