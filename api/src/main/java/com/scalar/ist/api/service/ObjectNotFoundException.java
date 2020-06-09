package com.scalar.ist.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Exception thrown when an object cannot be found in the database */
public class ObjectNotFoundException extends RuntimeException {
  private final List<String> ids = new ArrayList<>();
  private final String objectName;
  /**
   * Constructor
   *
   * @param objectClass the class of this object that cannot be found
   * @param id the first element of the id
   * @param ids the other elements of the id
   */
  public ObjectNotFoundException(Class objectClass, String id, String... ids) {
    this.objectName = objectClass.getSimpleName();
    this.ids.add(id);
    this.ids.addAll(Arrays.asList(ids));
  }

  public List<String> getIds() {
    return ids;
  }

  public String getObjectName() {
    return objectName;
  }

  @Override
  public String getMessage() {
    return "The object "
        + getObjectName()
        + " with the identifiers ("
        + String.join(",", getIds())
        + ") does not exist";
  }
}
