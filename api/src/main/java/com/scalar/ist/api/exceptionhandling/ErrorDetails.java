package com.scalar.ist.api.exceptionhandling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

/** Leaf object of {@link ErrorResponse}. */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class ErrorDetails {
  String field;
  String value;
  String issue;
  String location;
}
