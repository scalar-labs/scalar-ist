package com.scalar.ist.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Purpose {
  public static final String TABLE_NAME = "purpose";
  public static final String COMPANY_ID = "company_id";
  public static final String CREATED_AT = "created_at";
  public static final String ORGANIZATION_ID = "organization_id";
  public static final String CATEGORY_OF_PURPOSE = "category_of_purpose";
  public static final String PURPOSE_NAME = "purpose_name";
  public static final String DESCRIPTION = "description";
  public static final String LEGAL_TEXT = "legal_text";
  public static final String USER_FRIENDLY_TEXT = "user_friendly_text";
  public static final String GUIDANCE = "guidance";
  public static final String NOTE = "note";
  public static final String IS_ACTIVE = "is_active";
  public static final String CREATED_BY = "created_by";
  public static final String UPDATED_AT = "updated_at";
  String companyId;
  long createdAt;
  String organizationId;
  String categoryOfPurpose;
  String purposeName;
  String description;
  String legalText;
  String userFriendlyText;
  String guidance;
  String note;

  @JsonProperty(IS_ACTIVE)
  boolean active;

  String createdBy;
  long updatedAt;
}
