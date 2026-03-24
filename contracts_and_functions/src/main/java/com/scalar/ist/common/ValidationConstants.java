package com.scalar.ist.common;

public interface ValidationConstants {

  // Error messages
  String DISALLOWED_CONTRACT_EXECUTION_ORDER =
      "The contract is not allowed to execute in the specified order.";
  String HOLDER_ID_IS_NOT_MATCHED =
      "Executor's holder ID does not match with the one specified in the properties.";
  String PERMISSION_DENIED =
      "Permission is not granted due to inadequate roles or organization ids provided.";
  String HOLDER_ID_IS_MISSING =
      "Executor's holder ID is missing in the contract properties of the specified contract.";
  String ASSET_NAME_IS_MISSING =
      "Asset name is missing in the contract properties of the specified contract.";
  String PERMITTED_ASSET_NAMES_IS_MISSING =
      "Permitted asset names are missing in the contract properties of the specified contract.";
  String ASSET_ID_IS_NOT_PERMITTED =
      "The specified asset id is not one of the permitted asset names";
  String CONTRACT_ARGUMENT_SCHEMA_IS_MISSING =
      "Contract argument schema is not specified in the properties.";
  String INVALID_CONTRACT_ARGUMENTS_SCHEMA = "The schema provided is not valid.";
  String INVALID_CONTRACT_ARGUMENTS =
      "There is an error while validating the arguments.";
  String STATUS_UPDATE_NOT_ALLOWED = "The consent statement status update is not allowed";
  String ORGANIZATION_ALREADY_IN_USE = "The organization is already tied to a company.";
  String ASSET_IS_ALREADY_REGISTERED =
      "Asset provided is already registered in the database.";
  String RECORD_IS_ALREADY_REGISTERED =
      "Record provided is already registered in the database.";
  String ASSET_NOT_FOUND = "Asset is not found in the ledger.";
  String RECORD_NOT_FOUND = "Record is not found in the database.";
  String SALT_IS_MISSING = "Salt is not found in the properties.";
  String REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING =
      "Contract properties are not provided.";
  String EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID =
      "The specified executor company id does not match with the user profile company id.";
  String COMPANY_ID_DOES_NOT_MATCH_WITH_ASSET_COMPANY_ID =
      "The specified specified argument company id does not match with the asset company id.";
  String ORGANIZATION_ID_DOES_NOT_MATCH =
      "The specified argument organization id does not match with the consent statement organization id";
  String EXECUTION_RESTRICTED_TO_INITIALIZER_ACCOUNT =
      "Only the Initializer account can execute this contract";
  String REQUIRED_ATTRIBUTES_FOR_USER_PROFILE_ARE_MISSING =
      "The specified user profile has missing attributes of roles or organization_ids";
  String ASSET_SCHEMA_IS_MISSING =
      "Asset schema is missing in the contract properties of the specified contract.";
  String TABLE_SCHEMA_IS_MISSING =
      "Table schema is missing in the contract properties of the specified contract.";
}
