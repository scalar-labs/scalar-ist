package com.scalar.ist.api.exceptionhandling;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Value;

/**
 * JSON object definition of the body returned by a failed http request. Used by {@link
 * RestExceptionHandler}
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  public static final String NAME = "name";
  public static final String MESSAGE = "message";
  public static final String DEBUG_ID = "debugId";
  private final String name;
  private final List<ErrorDetails> details;
  private final String debugId;
  private final String message;
}
