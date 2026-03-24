package com.scalar.ist.common;

public class Constants
    implements ValidationConstants, CompanyConstants, ConsentConstants, SchemaConstants {

  // All constants are inherited from the four interfaces:
  // - ValidationConstants: general fields, record operations, validator, error messages
  // - CompanyConstants: company, organization, user profile, roles, third party
  // - ConsentConstants: consent statement, consent status
  // - SchemaConstants: asset schema, database schema

  private Constants() {}
}
