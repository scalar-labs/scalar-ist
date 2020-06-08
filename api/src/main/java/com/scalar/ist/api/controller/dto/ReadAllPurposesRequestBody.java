package com.scalar.ist.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReadAllPurposesRequestBody {
  long start;
  long end;

  @JsonProperty("is_inactive")
  boolean inactive;
}
