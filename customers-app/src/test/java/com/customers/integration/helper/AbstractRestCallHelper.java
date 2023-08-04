package com.customers.integration.helper;

import org.springframework.boot.test.web.client.TestRestTemplate;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractRestCallHelper {

	static final String PAGE_NUMBER = "pageNumber";
	static final String PAGE_SIZE = "pageSize";
	static final String NAME = "name";
	static final String FIRST_NAME = "firstName";
	static final String EMAIL = "email";
	static final String CITY = "city";
	static final String CUSTOMER_IDS = "customerIds";
	static final String CUSTOMER_ID = "customerId";
	static final String SEARCH = "search";

	protected final TestRestTemplate testRestTemplate;

}