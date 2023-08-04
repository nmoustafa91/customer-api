package com.customers.etag.exception;

import com.customers.exception.ValidationException;
import com.customers.exception.general.ApplicationError;

/**
 * It is a helper to handle the version again the request coming from the end users, check for the valid version provided in the header..
 *
 */
public class ETagPreconditionFailedException extends ValidationException {
  public ETagPreconditionFailedException(ApplicationError error) {
    super(error);
  }
}
