package com.scalar.ist.common;

public class Constants {

  // General json names
  public static final String NAMESPACE = "ist";
  public static final String ASSET_ID = "asset_id";
  public static final String ASSET_NAME = "asset_name";
  public static final String ASSET_VERSION = "asset_version";
  public static final String HOLDER_ID = "holder_id";
  public static final String HOLDER_ID_SYSADMIN = "sysadmin_holder_id";
  public static final String HOLDER_ID_SYSOPERATOR = "sysoperator_holder_id";
  public static final String ROLES = "roles";
  public static final String ROLES_REQUIRED = "roles_required";
  public static final String ORGANIZATIONS = "organizations";
  public static final String ORGANIZATION_ID = "organization_id";
  public static final String ORGANIZATION_IDS = "organization_ids";
  public static final String ORGANIZATION_IDS_REQUIRED = "organization_ids";
  public static final String ORGANIZATION_IDS_ARGUMENT = "organization_ids_argument";
  public static final String GROUP_COMPANY_IDS = "group_company_ids";
  public static final String COMPANY_ID = "company_id";
  public static final String CREATED_AT = "created_at";
  public static final String UPDATED_AT = "updated_at";
  public static final String GET_USER_PROFILE = "GetUserProfile";
  public static final String CONTRACT_ARGUMENT_SCHEMA = "contract_argument_schema";
  public static final String CREATED_BY = "created_by";
  public static final String ACTION = "action";
  public static final String DEACTIVATE_ACTION = "deactivate";
  public static final String UPSERT_USER_PROFILE = "UpsertUserProfile";
  public static final String UPDATE_ACTION = "update";
  public static final String INSERT_ACTION = "insert";
  public static final String ASSET_SCHEMA = "asset_schema";
  public static final String TABLE_SCHEMA = "table_schema";
  public static final String PROPERTIES = "properties";
  public static final String DATA_SUBJECT_ID = "data_subject_id";

  // Purpose related constant
  public static final String PURPOSE_ID = "purpose_id";
  public static final String PURPOSE_CATEGORY_OF_PURPOSE = "category_of_purpose";
  public static final String PURPOSE_NAME = "purpose_name";
  public static final String PURPOSE_LEGAL_TEXT = "legal_text";
  public static final String PURPOSE_USER_FRIENDLY_TEXT = "user_friendly_text";
  public static final String PURPOSE_GUIDANCE = "guidance";
  public static final String PURPOSE_NOTE = "note";
  public static final String PURPOSE_DESCRIPTION = "description";
  public static final String PURPOSE_TABLE = "purpose";

  // User roles
  public static final String ROLE_CONTROLLER = "Controller";
  public static final String ROLE_PROCESSOR = "Processor";
  public static final String ROLE_ADMINISTRATOR = "Admin";
  public static final String ROLE_SYSADMIN = "SysAdmin";
  public static final String ROLE_SYSOPERATOR = "SysOperator";

  // User Profile related constant
  public static final String USER_PROFILE_EXECUTOR_COMPANY_ID = "executor_company_id";
  public static final String USER_PROFILE_TABLE = "user_profile";
  public static final String USER_PROFILE_ROLES = "user_profile_roles";
  public static final String USER_PROFILE_ASSET_NAME = "user_profile_asset_name";
  public static final String USER_PROFILE_ASSET_VERSION = "user_profile_asset_version";

  // Register related constant
  public static final String REGISTER_COMPANY = "RegisterCompany";
  public static final String REGISTER_ORGANIZATION = "RegisterOrganization";

  // Record related json names
  public static final String RECORD_MODE = "mode";
  public static final String RECORD_MODE_INSERT = "insert";
  public static final String RECORD_MODE_UPDATE = "update";
  public static final String RECORD_MODE_UPSERT = "upsert";
  public static final String RECORD_DATA = "data";
  public static final String RECORD_SALT = "salt";
  public static final String GET_ASSET_RECORD = "GetAssetRecord";
  public static final String RECORD_IS_HASHED = "is_hashed";
  public static final String PUT_ASSET_RECORD = "PutAssetRecord";
  public static final String HASHED_ASSET_ID = "hashed_asset_id";
  public static final String HASHED_CONSENT_STATEMENT_ID = "hashed_consent_statement_id";

  // Company related json names
  public static final String EXECUTOR_COMPANY_ID = "executor_company_id";
  public static final String COMPANY_NAME = "company_name";
  public static final String CORPORATE_NUMBER = "corporate_number";
  public static final String COMPANY_METADATA = "company_metadata";
  public static final String ORGANIZATION_NAME = "organization_name";
  public static final String ORGANIZATION_DESCRIPTION = "organization_description";
  public static final String IS_ACTIVE = "is_active";
  public static final String ADMIN = "Admin";
  public static final String ADMINISTRATOR_ORGANIZATION = "Administrator organization";
  public static final String ORGANIZATION_METADATA = "organization_metadata";
  public static final String COMPANY_TABLE = "company";
  public static final String ORGANIZATION_TABLE = "organization";
  public static final String COMPANY_ADDRESS = "address";
  public static final String COMPANY_EMAIL = "email";
  public static final String COMPANY_ASSET_NAME = "company_asset_name";
  public static final String COMPANY_ASSET_VERSION = "company_asset_version";

  // Consent Statement related json names
  public static final String CONSENT_STATEMENT_DRAFT = "draft";
  public static final String CONSENT_STATEMENT_PUBLISHED = "published";
  public static final String CONSENT_STATEMENT_REVIEWED = "reviewed";
  public static final String CONSENT_STATEMENT_INACTIVE = "inactive";
  public static final String CONSENT_STATEMENT_VERSION = "version";
  public static final String CONSENT_STATEMENT_TITLE = "title";
  public static final String CONSENT_STATEMENT_BENEFIT_IDS = "benefit_ids";
  public static final String CONSENT_STATEMENT_STATUS = "status";
  public static final String CONSENT_STATEMENT_DRAFT_STATUS = "draft";
  public static final String CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES = "optional_third_parties";
  public static final String CONSENT_STATEMENT_OPTIONAL_PURPOSES = "optional_purposes";
  public static final String CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS = "data_set_schema_ids";
  public static final String CONSENT_STATEMENT_ABSTRACT = "abstract";
  public static final String CONSENT_STATEMENT_THIRD_PARTY_IDS = "third_party_ids";
  public static final String CONSENT_STATEMENT_PURPOSE_IDS = "purpose_ids";
  public static final String CONSENT_STATEMENT = "consent_statement";
  public static final String CONSENT_STATEMENT_CHANGES = "changes";
  public static final String CONSENT_STATEMENT_TABLE = "consent_statement";
  public static final String CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID =
      "data_retention_policy_id";
  public static final String CONSENT_STATEMENT_ROOT_CONSENT_STATEMENT_ID =
      "root_consent_statement_id";
  public static final String CONSENT_STATEMENT_ID = "consent_statement_id";
  public static final String CONSENT_STATEMENT_PARENT_ID = "parent_consent_statement_id";
  public static final String CONSENT_STATEMENT_DESCRIPTION = "description";
  public static final String CONSENT_STATEMENT_CONTENTS = "consent_statement_contents";

  // Consent related json names
  public static final String CONSENT = "consent";
  public static final String CONSENT_TABLE = "consent";
  public static final String CONSENT_STATUS = "consent_status";
  public static final String CONSENTED_DETAIL = "consented_detail";
  public static final String REJECTED_DETAIL = "rejected_detail";
  public static final String CONSENT_ID = "consent_id";
  public static final String CONSENT_STATUS_APPROVED = "approved";
  public static final String CONSENT_STATUS_REJECTED = "rejected";
  public static final String CONSENT_STATUS_CONFIGURED = "configured";

  // Retention Policy related json names
  public static final String RETENTION_POLICY_NAME = "policy_name";
  public static final String RETENTION_POLICY_TYPE = "policy_type";
  public static final String RETENTION_POLICY_USE_DURATION = "length_of_use";
  public static final String RETENTION_POLICY_RETENTION_DURATION = "length_of_retention";
  public static final String RETENTION_POLICY_DESCRIPTION = "description";
  public static final String RETENTION_POLICY_TABLE = "data_retention_policy";

  // Dataset Schema related json names
  public static final String DATA_SET_SCHEMA = "data_set_schema";
  public static final String DATA_SET_SCHEMA_ID = "data_set_schema_id";
  public static final String DATA_SET_NAME = "data_set_name";
  public static final String DATA_SET_DESCRIPTION = "description";
  public static final String DATA_CATEGORY = "category_of_data";
  public static final String DATA_TYPE = "data_type";
  public static final String DATA_CLASSIFICATION = "classification";
  public static final String DATA_SET_SCHEMA_CHANGES = "changes";
  public static final String DATA_LOCATION = "data_location";
  public static final String DATA_SET_SCHEMA_TABLE = "data_set_schema";

  // Validator related json names
  public static final String VALIDATE_ARGUMENT_CONTRACT_ARGUMENT = "contract_argument";
  public static final String VALIDATE_ARGUMENT_SCHEMA = "schema";
  public static final String VALIDATE_ARGUMENT = "ValidateArgument";
  public static final String VALIDATE_PERMISSION = "ValidatePermission";

  // Third Party related json names
  public static final String THIRD_PARTY_ID = "third_party_id";
  public static final String THIRD_PARTY_DESCRIPTION = "description";
  public static final String THIRD_PARTY_DOMAIN = "third_party_domain";
  public static final String THIRD_PARTY_NAME = "third_party_name";
  public static final String THIRD_PARTY_METADATA = "third_party_metadata";
  public static final String THIRD_PARTY_TABLE = "third_party";

  // Benefit related json names
  public static final String BENEFIT_ID = "benefit_id";

  // Initalize related json names
  public static final String INITIALIZER_ACCOUNT_NAME = "Initializer";

  // Database schema related json names
  public static final String DB_VALUE_TYPE = "type";
  public static final String DB_VALUE_NAME = "name";
  public static final String DB_VALUE_BIGINT = "bigint";
  public static final String DB_VALUE_TEXT = "text";
  public static final String DB_VALUE_BOOLEAN = "boolean";
  public static final String DB_VALUE_FLOAT = "float";
  public static final String DB_VALUE_INT = "int";
  public static final String DB_VALUE_BLOB = "blob";
  public static final String DB_TABLE_PARTITION_KEYS = "partition_keys";
  public static final String DB_TABLE_CLUSTERING_KEYS = "clustering_keys";
  public static final String DB_TABLE_COLUMNS = "columns";
  public static final String DB_TABLE_NAME = "table_name";

  // Asset schema related json names
  public static final String ASSET_STRING_TYPE = "string";
  public static final String ASSET_BOOLEAN_TYPE = "boolean";
  public static final String ASSET_ARRAY_TYPE = "array";
  public static final String ASSET_OBJECT_TYPE = "object";
  public static final String ASSET_NUMBER_TYPE = "number";
  public static final String ASSET_INTEGER_TYPE = "integer";
  public static final String ASSET_DEFAULT_VALUE = "default";
  public static final String ASSET_TYPE = "type";
  public static final String ASSET_TYPE_PATTERN = "pattern";
  // Error messages
  public static final String DISALLOWED_CONTRACT_EXECUTION_ORDER =
      "The contract is not allowed to execute in the specified order.";
  public static final String HOLDER_ID_IS_NOT_MATCHED =
      "Executor's holder ID does not match with the one specified in the properties.";
  public static final String PERMISSION_DENIED =
      "Permission is not granted due to inadequate roles or organization ids provided.";
  public static final String HOLDER_ID_IS_MISSING =
      "Executor's holder ID is missing in the contract properties of the specified contract.";
  public static final String ASSET_NAME_IS_MISSING =
      "Asset name is missing in the contract properties of the specified contract.";
  public static final String ASSET_VERSION_IS_MISSING =
      "Asset version is missing in the contract properties of the specified contract.";
  public static final String CONTRACT_ARGUMENT_SCHEMA_IS_MISSING =
      "Contract argument schema is not specified in the properties.";
  public static final String STATUS_UPDATE_NOT_ALLOWED =
      "The consent statement status update is not allowed";
  public static final String INVALID_CONTRACT_ARGUMENTS_SCHEMA =
      "The schema provided is not valid.";
  public static final String ORGANIZATION_ALREADY_IN_USE =
      "The organization is already tied to a company.";
  public static final String INVALID_CONTRACT_ARGUMENTS =
      "There is an error while validating the arguments.";
  public static final String REQUIRED_ATTRIBUTES_FOR_USER_PROFILE_ARE_MISSING =
      "The specified user profile has missing attributes of roles or organization_ids";
  public static final String ASSET_IS_ALREADY_REGISTERED =
      "Asset provided is already registered in the database.";
  public static final String RECORD_IS_ALREADY_REGISTERED =
      "Record provided is already registered in the database.";
  public static final String ASSET_NOT_FOUND = "Asset is not found in the ledger.";
  public static final String RECORD_NOT_FOUND = "Record is not found in the database.";
  public static final String SALT_IS_MISSING = "Salt is not found in the properties.";
  public static final String REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING =
      "Contract properties are not provided.";
  public static final String EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID =
      "The specified executor company id does not match with the user profile company id.";
  public static final String ORGANIZATION_ID_DOES_NOT_MATCH =
      "The specified argument organization id does not match with the consent statement organization id";
  public static final String EXECUTION_RESTRICTED_TO_INITIALIZER_ACCOUNT =
      "Only the " + INITIALIZER_ACCOUNT_NAME + " account can execute this contract";
  public static final String ASSET_SCHEMA_IS_MISSING =
      "Asset schema is missing in the contract properties of the specified contract.";
  public static final String TABLE_SCHEMA_IS_MISSING =
      "Table schema is missing in the contract properties of the specified contract.";
}
