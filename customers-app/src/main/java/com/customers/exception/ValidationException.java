package com.customers.exception;

import com.customers.exception.general.ApplicationError;
import com.customers.exception.general.ApplicationErrorException;

public class ValidationException extends ApplicationErrorException {

  public ValidationException(final ApplicationError error) { super(error); }

}
