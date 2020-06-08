package com.scalar.ist.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class DataSetSchema {
  public static final String TABLE_NAME = "dataset_schema";
  public static final String COMPANY_ID = "company_id";
  public static final String CREATED_AT = "created_at";
  public static final String ORGANIZATION_ID = "organization_id";
  public static final String DATA_SET_NAME = "data_set_name";
  public static final String DESCRIPTION = "description";
  public static final String DATA_LOCATION = "data_location";
  public static final String CATEGORY_OF_DATA = "category_of_data";
  public static final String DATA_TYPE = "data_type";
  public static final String CLASSIFICATION = "classification";
  public static final String DATA_SET_SCHEMA = "data_set_schema";
  public static final String IS_ACTIVE = "is_active";
  public static final String CREATED_BY = "created_by";
  public static final String UPDATED_AT = "updated_at";
  String companyId;
  long createdAt;
  String organizationId;
  String dataSetName;
  String description;
  DataLocation dataLocation;
  String categoryOfData;
  String dataType;
  String classification;
  JsonNode dataSetSchema;

  @JsonProperty(IS_ACTIVE)
  boolean active;

  String createdBy;
  long updatedAt;

  @Builder
  @Value
  public static class DataLocation {
    String uri;
    String connect;
    String authenticate;
  }
}
