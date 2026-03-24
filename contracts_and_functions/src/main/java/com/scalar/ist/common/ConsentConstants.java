package com.scalar.ist.common;

public interface ConsentConstants {

  // Consent Statement related json names
  String CONSENT_STATEMENT_DRAFT = "draft";
  String CONSENT_STATEMENT_PUBLISHED = "published";
  String CONSENT_STATEMENT_REVIEWED = "reviewed";
  String CONSENT_STATEMENT_INACTIVE = "inactive";
  String CONSENT_STATEMENT_VERSION = "version";
  String CONSENT_STATEMENT_TITLE = "title";
  String CONSENT_STATEMENT_BENEFIT_IDS = "benefit_ids";
  String CONSENT_STATEMENT_STATUS = "status";
  String CONSENT_STATEMENT_DRAFT_STATUS = "draft";
  String CONSENT_STATEMENT_OPTIONAL_THIRD_PARTIES = "optional_third_parties";
  String CONSENT_STATEMENT_OPTIONAL_PURPOSES = "optional_purposes";
  String CONSENT_STATEMENT_DATA_SET_SCHEMA_IDS = "data_set_schema_ids";
  String CONSENT_STATEMENT_ABSTRACT = "abstract";
  String CONSENT_STATEMENT_THIRD_PARTY_IDS = "third_party_ids";
  String CONSENT_STATEMENT_PURPOSE_IDS = "purpose_ids";
  String CONSENT_STATEMENT = "consent_statement";
  String CONSENT_STATEMENT_CHANGES = "changes";
  String CONSENT_STATEMENT_DATA_RETENTION_POLICY_ID = "data_retention_policy_id";
  String CONSENT_STATEMENT_ROOT_CONSENT_STATEMENT_ID = "root_consent_statement_id";
  String CONSENT_STATEMENT_ID = "consent_statement_id";
  String CONSENT_STATEMENT_PARENT_ID = "parent_consent_statement_id";
  String CONSENT_STATEMENT_DESCRIPTION = "description";

  // Consent status related json names
  String CONSENT_STATUS = "consent_status";
  String CONSENTED_DETAIL = "consented_detail";
  String REJECTED_DETAIL = "rejected_detail";
  String CONSENT_ID = "consent_id";
  String CONSENT_STATUS_APPROVED = "approved";

  // Third Party related json names
  String THIRD_PARTY_ID = "third_party_id";
  String THIRD_PARTY_DESCRIPTION = "description";
  String THIRD_PARTY_DOMAIN = "third_party_domain";
  String THIRD_PARTY_NAME = "third_party_name";
  String THIRD_PARTY_METADATA = "third_party_metadata";

  // Benefit related json names
  String BENEFIT_ID = "benefit_id";

  // Data subject
  String DATA_SUBJECT_ID = "data_subject_id";

  // Group company
  String GROUP_COMPANY_IDS = "group_company_ids";

  // Hashed identifiers
  String HASHED_CONSENT_STATEMENT_ID = "hashed_consent_statement_id";
}
