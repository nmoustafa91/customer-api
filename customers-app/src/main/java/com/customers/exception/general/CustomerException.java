package com.customers.exception.general;

import lombok.ToString;

@ToString(callSuper=true)
public class CustomerException extends RuntimeException {

  public CustomerException(String message, Throwable cause) {
    super(message, cause);
  }

}
