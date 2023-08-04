package com.customers.etag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ETagResponseEntity<T> {

	ResponseEntity<T> ok();

	ResponseEntity<T> httpStatus(HttpStatus httpStatus);

}
