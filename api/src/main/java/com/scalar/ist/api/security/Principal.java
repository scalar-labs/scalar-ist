package com.scalar.ist.api.security;

import lombok.Builder;
import lombok.Value;

/**
 * Object which holds the authenticated user information
 */
@Value
@Builder
public class Principal {
  String holderId;
  String companyId;
}
