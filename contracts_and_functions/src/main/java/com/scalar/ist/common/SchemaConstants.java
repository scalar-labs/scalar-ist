package com.scalar.ist.common;

public interface SchemaConstants {

  // Namespace
  String NAMESPACE = "ist";

  // Table names
  String CONSENT_STATEMENT_TABLE = "consent_statement";
  String CONSENT_TABLE = "consent";
  String COMPANY_TABLE = "company";
  String ORGANIZATION_TABLE = "organization";
  String USER_PROFILE_TABLE = "user_profile";
  String THIRD_PARTY_TABLE = "third_party";

  // Contract and subcontract identifiers
  String CONTRACT_ARGUMENT_SCHEMA = "contract_argument_schema";
  String VALIDATE_ARGUMENT_CONTRACT_ARGUMENT = "contract_argument";
  String VALIDATE_ARGUMENT_SCHEMA = "schema";
  String VALIDATE_ARGUMENT = "ValidateArgument";
  String VALIDATE_PERMISSION = "ValidatePermission";
  String GET_USER_PROFILE = "GetUserProfile";
  String GET_ASSET_RECORD = "GetAssetRecord";
  String PUT_ASSET_RECORD = "PutAssetRecord";

  // Asset related json names
  String ASSET_ID = "asset_id";
  String ASSET_NAME = "asset_name";
  String ASSET_VERSION = "asset_version";
  String PERMITTED_ASSET_NAMES = "permitted_asset_names";
  String HASHED_ASSET_ID = "hashed_asset_id";

  // Record related json names
  String RECORD_MODE = "mode";
  String RECORD_MODE_INSERT = "insert";
  String RECORD_MODE_UPDATE = "update";
  String RECORD_MODE_UPSERT = "upsert";
  String RECORD_MODE_GET = "get";
  String RECORD_MODE_SCAN = "scan";
  String RECORD_DATA = "data";
  String RECORD_SALT = "salt";
  String RECORD_START_VERSION = "start_version";
  String RECORD_END_VERSION = "end_version";
  String RECORD_LIMIT = "with_limit";
  String RECORD_VERSIONS = "versions";
  String RECORD_VERSION = "version";
  String RECORD_VERSION_ORDER = "version_order";
  String RECORD_IS_HASHED = "is_hashed";

  // Action related json names
  String ACTION = "action";
  String UPDATE_ACTION = "update";
  String INSERT_ACTION = "insert";
  String PROPERTIES = "properties";

  // Asset schema related json names
  String ASSET_SCHEMA = "asset_schema";
  String TABLE_SCHEMA = "table_schema";
  String ASSET_STRING_TYPE = "string";
  String ASSET_BOOLEAN_TYPE = "boolean";
  String ASSET_ARRAY_TYPE = "array";
  String ASSET_OBJECT_TYPE = "object";
  String ASSET_NUMBER_TYPE = "number";
  String ASSET_INTEGER_TYPE = "integer";
  String ASSET_DEFAULT_VALUE = "default";
  String ASSET_TYPE = "type";
  String ASSET_TYPE_PATTERN = "pattern";

  // Database schema related json names
  String DB_VALUE_TYPE = "type";
  String DB_VALUE_NAME = "name";
  String DB_VALUE_BIGINT = "bigint";
  String DB_VALUE_TEXT = "text";
  String DB_VALUE_BOOLEAN = "boolean";
  String DB_VALUE_FLOAT = "float";
  String DB_VALUE_INT = "int";
  String DB_VALUE_BLOB = "blob";
  String DB_TABLE_PARTITION_KEYS = "partition_keys";
  String DB_TABLE_CLUSTERING_KEYS = "clustering_keys";
  String DB_TABLE_COLUMNS = "columns";
  String DB_TABLE_NAME = "table_name";
}
