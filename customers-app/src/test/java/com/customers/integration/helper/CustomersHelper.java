package com.customers.integration.helper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import com.customers.model.BulkCreationRequestDTO;
import com.customers.model.BulkUpdateRequestDTO;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.UpdateCustomerRequestDTO;

public class CustomersHelper extends AbstractRestCallHelper {

  private static final String CUSTOMERS_URL = "/customers";
  private static final String CUSTOMER_URL = "/customers/{customerId}";
  private static final String CUSTOMERS_BULK_URL = "/customers/bulk";
  private static final String ALL_CUSTOMERS_URL = "/customers/all-customers";
  private static final String SINGLE_CUSTOMER_URL = "/customers/single-customer";

  public CustomersHelper(TestRestTemplate testRestTemplate) {
    super(testRestTemplate);
  }

  public ResponseEntity<CustomerDTO> create(CreateCustomerRequestDTO request) {
    return testRestTemplate.postForEntity(CUSTOMERS_URL, request, CustomerDTO.class);
  }

  public ResponseEntity<CustomerDTO> getCustomer(UUID customerId) {
    return testRestTemplate.getForEntity(CUSTOMER_URL, CustomerDTO.class, customerId);
  }

  public void getCustomer(UUID customerId, HttpStatus expectedStatus) {
    var response = testRestTemplate.getForEntity(CUSTOMER_URL, Object.class, customerId);
    assertThat(response.getStatusCode(), is(expectedStatus));
  }

  public ResponseEntity<CustomerDTO> update(UpdateCustomerRequestDTO request, UUID customerId) {
    String version = getCustomer(customerId).getHeaders().getETag();
    HttpHeaders headers = new HttpHeaders();
    headers.add("If-Match", version);
    return testRestTemplate.exchange(CUSTOMER_URL, HttpMethod.PUT, new HttpEntity<>(request, headers), CustomerDTO.class,
        customerId);
  }

  public void update(UpdateCustomerRequestDTO request, UUID customerId, HttpStatus expectedStatus) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("If-Match", "0");
    var response = testRestTemplate.exchange(CUSTOMER_URL, HttpMethod.PUT, new HttpEntity<>(request, headers), Object.class, customerId);
    assertThat(response.getStatusCode(), is(expectedStatus));
  }

  public ResponseEntity<ListCustomersResponseDTO> getCustomers(String name, String firstName, List<String> customerIds, String email,
      String city, String search, Pageable pageRequest) {
    Map<String, String> requestParameters = new HashMap<>();
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(CUSTOMERS_URL + "?");
    addQueryParamIfNotNull(requestParameters, urlBuilder, NAME, name);
    addQueryParamIfNotNull(requestParameters, urlBuilder, FIRST_NAME, firstName);
    addQueryParamIfNotNull(requestParameters, urlBuilder, CUSTOMER_IDS, customerIds);
    addQueryParamIfNotNull(requestParameters, urlBuilder, CITY, city);
    addQueryParamIfNotNull(requestParameters, urlBuilder, EMAIL, email);
    addQueryParamIfNotNull(requestParameters, urlBuilder, SEARCH, search);
    if (pageRequest != null) {
      urlBuilder.append(PAGE_NUMBER + "={" + PAGE_NUMBER + "}&");
      urlBuilder.append(PAGE_SIZE + "={" + PAGE_SIZE + "}&");
      requestParameters.put(PAGE_NUMBER, String.valueOf(pageRequest.getPageNumber()));
      requestParameters.put(PAGE_SIZE, String.valueOf(pageRequest.getPageSize()));
    }
    final String builderContent = urlBuilder.toString();
    final String url = builderContent.substring(0, (builderContent.length() - 1));
    return testRestTemplate.getForEntity(url, ListCustomersResponseDTO.class, requestParameters);
  }

  public ResponseEntity<ListCustomersResponseDTO> getAllCustomers(Pageable pageRequest) {
    Map<String, String> requestParameters = new HashMap<>();
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(ALL_CUSTOMERS_URL + "?");
    if (pageRequest != null) {
      urlBuilder.append(PAGE_NUMBER + "={" + PAGE_NUMBER + "}&");
      urlBuilder.append(PAGE_SIZE + "={" + PAGE_SIZE + "}&");
      requestParameters.put(PAGE_NUMBER, String.valueOf(pageRequest.getPageNumber()));
      requestParameters.put(PAGE_SIZE, String.valueOf(pageRequest.getPageSize()));
    }
    final String builderContent = urlBuilder.toString();
    final String url = builderContent.substring(0, (builderContent.length() - 1));
    return testRestTemplate.getForEntity(url, ListCustomersResponseDTO.class, requestParameters);
  }

  public ResponseEntity<CustomerDTO> getSingleFilteredCustomer(String name, String firstName, String customerId) {
    Map<String, String> requestParameters = new HashMap<>();
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(SINGLE_CUSTOMER_URL + "?");
    addQueryParamIfNotNull(requestParameters, urlBuilder, NAME, name);
    addQueryParamIfNotNull(requestParameters, urlBuilder, FIRST_NAME, firstName);
    addQueryParamIfNotNull(requestParameters, urlBuilder, CUSTOMER_ID, customerId);
    final String builderContent = urlBuilder.toString();
    final String url = builderContent.substring(0, (builderContent.length() - 1));
    return testRestTemplate.getForEntity(url, CustomerDTO.class, requestParameters);
  }

  public void  deleteCustomer(UUID customerId) {
    testRestTemplate.delete(CUSTOMER_URL, customerId);
  }

  public ResponseEntity<List> createBulk(BulkCreationRequestDTO request) {
    return testRestTemplate.postForEntity(CUSTOMERS_BULK_URL, request, List.class);
  }

  public ResponseEntity<List> updateBulk(BulkUpdateRequestDTO request) {
    return testRestTemplate.exchange(CUSTOMERS_BULK_URL, HttpMethod.PUT, new HttpEntity<>(request), List.class);
  }

  public void updateBulk(BulkUpdateRequestDTO request, HttpStatus expectedStatus) {
    var response = testRestTemplate.exchange(CUSTOMERS_BULK_URL, HttpMethod.PUT, new HttpEntity<>(request), Object.class);
    assertThat(response.getStatusCode(), is(expectedStatus));
  }

  public ResponseEntity<Void> deleteCustomersBulk(List<UUID> customerIds) {
    return testRestTemplate.exchange(CUSTOMERS_BULK_URL, HttpMethod.DELETE, new HttpEntity<>(customerIds), void.class, new HashMap<>());
  }

  private void addQueryParamIfNotNull(Map<String, String> requestParameters, StringBuilder urlBuilder, String paramName, List<String> paramValues) {
    if (!CollectionUtils.isEmpty(paramValues)) {
      urlBuilder.append(paramName).append("={").append(paramName).append("}&");
      requestParameters.put(paramName, String.join(",", paramValues));
    }
  }

  private void addQueryParamIfNotNull(Map<String, String> requestParameters, StringBuilder urlBuilder, String paramName, String paramValue) {
    if (paramValue != null) {
      urlBuilder.append(paramName).append("={").append(paramName).append("}&");
      requestParameters.put(paramName, paramValue);
    }
  }
}
