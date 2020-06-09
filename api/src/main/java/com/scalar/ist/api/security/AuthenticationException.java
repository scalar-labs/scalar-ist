package com.scalar.ist.api.security;

public class AuthenticationException extends RuntimeException {
  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }

  public AuthenticationException(String message) {
    super(message);
  }
}
