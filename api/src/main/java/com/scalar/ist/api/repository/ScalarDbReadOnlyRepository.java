package com.scalar.ist.api.repository;

import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.exception.transaction.CrudException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

/**
 * A generic repository base class for read-only purpose
 * @param <T> the data class
 */
public abstract class ScalarDbReadOnlyRepository<T> {
  private final DistributedTransactionManager db;

  public ScalarDbReadOnlyRepository(DistributedTransactionManager db) {
    this.db = db;
  }

  public Optional<T> get(Get get) {
    try {
      Optional<Result> result = db.start().get(get);
      return result.map(this::parse);
    } catch (CrudException e) {
      throw new RepositoryException("Error getting the record for " + get, e);
    }
  }

  public List<T> scan(Scan scan) {
    try {
      List<Result> results = db.start().scan(scan);
      return results.stream().map(this::parse).collect(Collectors.toList());
    } catch (CrudException e) {
      throw new RepositoryException("Error scanning the record for " + scan, e);
    }
  }

  /**
   * Convert a Scalar DB query result to an object of the data class
   * @param result the Scalar DB query result
   * @return an object of the data class
   */
  public abstract T parse(@NotNull Result result);
}
