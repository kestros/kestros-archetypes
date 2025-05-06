package com.test.buildintegrationtest.api.exceptions;

import javax.annotation.Nonnull;

/**
 * Exception thrown when an error occurs retrieving Sample Models.
 */
public class SampleModelRetrievalException extends Exception {

  /**
   * Sample Model Retrieval Exception.
   *
   * @param message Exception message.
   */
  public SampleModelRetrievalException(@Nonnull String message) {
    super(message);
  }

  /**
   * Sample Model Retrieval Exception.
   *
   * @param message Exception message.
   * @param cause Exception cause.
   */
  public SampleModelRetrievalException(@Nonnull String message, @Nonnull Throwable cause) {
    super(message, cause);
  }

  /**
   * Sample Model Retrieval Exception.
   *
   * @param cause Exception cause.
   */
  public SampleModelRetrievalException(@Nonnull Throwable cause) {
    super(cause);
  }
}
