package com.scalar.ist.common;

public interface CompanyConstants {

  // Company related json names
  String COMPANY_ID = "company_id";
  String EXECUTOR_COMPANY_ID = "executor_company_id";
  String COMPANY_NAME = "company_name";
  String CORPORATE_NUMBER = "corporate_number";
  String COMPANY_METADATA = "company_metadata";
  String COMPANY_ASSET_NAME = "company_asset_name";
  String COMPANY_ASSET_VERSION = "company_asset_version";
  String COMPANY_ADDRESS = "address";
  String COMPANY_EMAIL = "email";

  // Organization related json names
  String ORGANIZATIONS = "organizations";
  String ORGANIZATION_ID = "organization_id";
  String ORGANIZATION_IDS = "organization_ids";
  String ORGANIZATION_IDS_REQUIRED = "organization_ids";
  String ORGANIZATION_IDS_ARGUMENT = "organization_ids_argument";
  String ORGANIZATION_NAME = "organization_name";
  String ORGANIZATION_DESCRIPTION = "organization_description";
  String ORGANIZATION_METADATA = "organization_metadata";
  String IS_ACTIVE = "is_active";
  String ADMIN = "Admin";
  String ADMINISTRATOR_ORGANIZATION = "Administrator organization";

  // User roles
  String ROLES = "roles";
  String ROLES_REQUIRED = "roles_required";
  String ROLE_CONTROLLER = "Controller";
  String ROLE_PROCESSOR = "Processor";
  String ROLE_ADMINISTRATOR = "Admin";
  String ROLE_SYSADMIN = "SysAdmin";
  String ROLE_SYSOPERATOR = "SysOperator";

  // User Profile related constants
  String HOLDER_ID = "holder_id";
  String HOLDER_ID_SYSADMIN = "sysadmin_holder_id";
  String HOLDER_ID_SYSOPERATOR = "sysoperator_holder_id";
  String USER_PROFILE_EXECUTOR_COMPANY_ID = "executor_company_id";
  String USER_PROFILE_ROLES = "user_profile_roles";
  String USER_PROFILE_ASSET_NAME = "user_profile_asset_name";
  String USER_PROFILE_ASSET_VERSION = "user_profile_asset_version";

  // Timestamps
  String CREATED_AT = "created_at";
  String CREATED_BY = "created_by";
  String UPDATED_AT = "updated_at";

  // Initialize related json names
  String INITIALIZER_ACCOUNT_NAME = "Initializer";

  // Register related constants
  String REGISTER_COMPANY = "RegisterCompany";
  String REGISTER_ORGANIZATION = "RegisterOrganization";
  String UPSERT_USER_PROFILE = "UpsertUserProfile";
}
