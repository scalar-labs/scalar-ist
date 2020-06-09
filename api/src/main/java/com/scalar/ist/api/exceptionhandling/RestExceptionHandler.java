package com.scalar.ist.api.exceptionhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.ist.api.security.AuthenticationException;
import com.scalar.ist.api.service.ObjectNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handle runtime exception thrown during a http request call so as to return a standard error
 * message.{@link ErrorResponse}
 */
@ControllerAdvice
public class RestExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
  private final Logger log = LogManager.getLogger();
  private final ObjectMapper objectMapper;

  @Autowired
  public RestExceptionHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Handle {@link org.springframework.security.core.AuthenticationException}. This is triggered
   * when a request to secured endpoints without credentials is received.
   */
  @ResponseBody
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      org.springframework.security.core.AuthenticationException e) {
    ErrorResponse.ErrorResponseBuilder error = logError(request, e);
    error.name("No credentials provided");
    error.message(e.getMessage());
    writeErrorResponse(response, error.build(), HttpServletResponse.SC_UNAUTHORIZED);
  }

  /**
   * Handle {@link org.springframework.security.access.AccessDeniedException}. This is triggered
   * when the provided role does not have the permission to access the endpoint.
   */
  @ResponseBody
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      org.springframework.security.access.AccessDeniedException e) {
    ErrorResponse.ErrorResponseBuilder error = logError(request, e);
    error.name("Invalid role");
    error.message(e.getMessage());
    writeErrorResponse(response, error.build(), HttpServletResponse.SC_FORBIDDEN);
  }

  @ResponseBody
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Object> handleMissingRequestParameterError(
      HttpServletRequest request, MissingServletRequestParameterException e) {
    return internalHandler(
        request, e, HttpStatus.BAD_REQUEST, "Missing parameter", e.getMessage(), null);
  }

  @ResponseBody
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolationError(
      HttpServletRequest request, ConstraintViolationException e) {
    ArrayList<ErrorDetails> errorDetails = new ArrayList<>();
    e.getConstraintViolations()
        .forEach(
            cv ->
                errorDetails.add(
                    ErrorDetails.builder()
                        .field(
                            cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : null)
                        .issue(cv.getMessage())
                        .value(
                            cv.getInvalidValue() != null ? cv.getInvalidValue().toString() : null)
                        .build()));
    return internalHandler(
        request, e, HttpStatus.BAD_REQUEST, "Validation error", e.getMessage(), errorDetails);
  }

  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleMethodArgumentNotValidError(
      HttpServletRequest request, MethodArgumentNotValidException e) {
    ArrayList<ErrorDetails> errorDetails = new ArrayList<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(
            fe ->
                errorDetails.add(
                    ErrorDetails.builder()
                        .field(fe.getField())
                        .issue(fe.getDefaultMessage())
                        .value(
                            fe.getRejectedValue() != null ? fe.getRejectedValue().toString() : null)
                        .build()));
    return internalHandler(
        request,
        e,
        HttpStatus.BAD_REQUEST,
        "Validation error",
        "Validation failed for " + e.getBindingResult().getObjectName(),
        errorDetails);
  }

  @ResponseBody
  @ExceptionHandler(ObjectNotFoundException.class)
  public ResponseEntity<Object> handleObjectNotFoundError(
      HttpServletRequest request, ObjectNotFoundException e) {
    return internalHandler(
        request, e, HttpStatus.NOT_FOUND, "Object not found", e.getMessage(), null);
  }

  @ResponseBody
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> handleAccessDeniedError(
      HttpServletRequest request, AccessDeniedException e) {
    return internalHandler(
        request, e, HttpStatus.FORBIDDEN, "Access is denied", e.getMessage(), null);
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleServerError(HttpServletRequest request, Exception e) {
    return internalHandler(
        request,
        e,
        HttpStatus.INTERNAL_SERVER_ERROR,
        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
        e.getMessage(),
        null);
  }

  /**
   * Create a JSON error response to be returned in the response body
   *
   * @param request the http request
   * @param e the exception
   * @param status the http error status
   * @param name a short description of the error
   * @param message a more detailed message
   * @param details optional details
   * @return a JSON response entity
   */
  private ResponseEntity<Object> internalHandler(
      HttpServletRequest request,
      Exception e,
      HttpStatus status,
      String name,
      String message,
      @Nullable List<ErrorDetails> details) {
    ErrorResponse.ErrorResponseBuilder error = logError(request, e);
    error.name(name);
    error.message(message);
    if (details != null) {
      error.details(details);
    }
    return new ResponseEntity<>(error.build(), status);
  }

  /**
   * Log the exception and prepare the error object to be returned in the response body
   *
   * @param request the http request
   * @param e the exception
   * @return an error builder with the "debugId" already set
   */
  private ErrorResponse.ErrorResponseBuilder logError(HttpServletRequest request, Exception e) {
    ErrorResponse.ErrorResponseBuilder error = ErrorResponse.builder();
    String bugId = UUID.randomUUID().toString();
    error.debugId(bugId);
    log.error(
        String.format(
            "Request %s '%s' raised an error (error id : %s)",
            request.getMethod(), request.getRequestURI(), bugId),
        e);
    return error;
  }

  @ExceptionHandler(AuthenticationException.class)
  public void handleAuthenticationError(
      HttpServletRequest request, HttpServletResponse response, Exception e) {
    ErrorResponse.ErrorResponseBuilder error = logError(request, e);
    error.name("Error validating the credentials");
    error.message(e.getMessage());
    writeErrorResponse(response, error.build(), HttpServletResponse.SC_UNAUTHORIZED);
  }

  private void writeErrorResponse(
      HttpServletResponse response, ErrorResponse errorResponse, int errorCode) {
    try {
      response.getWriter().print(objectMapper.writeValueAsString(errorResponse));
    } catch (IOException e) {
      log.error(
          String.format(
              "Failed to write the error details to the request response : %s",
              errorResponse.getDebugId()));
    }
    response.setStatus(errorCode);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
  }
}
