package com.customers.exception;

import com.customers.exception.general.ApplicationError;
import com.customers.exception.general.ApplicationErrorException;

public class NotFoundException extends ApplicationErrorException {

  public NotFoundException(final ApplicationError error) { super(error); }

}
