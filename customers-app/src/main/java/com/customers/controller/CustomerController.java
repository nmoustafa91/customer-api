package com.customers.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.customers.api.CustomersApi;
import com.customers.db.model.VersionModel;
import com.customers.etag.CustomerETagResponseEntity;
import com.customers.model.BulkCreationRequestDTO;
import com.customers.model.BulkUpdateRequestDTO;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.UpdateCustomerRequestDTO;
import com.customers.service.CustomerService;

import lombok.RequiredArgsConstructor;

/**
 * This is the controller layer to handle the client request for the customers api.
 * It implements customers Api interface which is generated already using open api generation, so wo don't have to specify everything explicitly
 * in the code by annotations, but I prepared the open api and the DTOs and the API were generated based on what I have defined in openapi yaml file.
 */
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

	private final CustomerService customerservice;

	@Override
	public ResponseEntity<CustomerDTO> createCustomer(CreateCustomerRequestDTO createCustomerRequestDTO) {
		CustomerDTO customerDTO = customerservice.createCustomer(createCustomerRequestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(customerDTO);
	}

	/**
	 * It is a general filtering endpoint to facilitate the searching and filtering to the end users, it could be extended but I just provided a simple version.
	 * Pagination is considered
	 *
	 * @param name Customer name query parameter (optional)
	 * @param firstName Customer first name query parameter (optional)
	 * @param customerIds Customers ids query parameter, can be comma-separated list to include multiple values (optional)
	 * @param city Customer city query parameter (optional)
	 * @param email Customer email query parameter (optional)
	 * @param search Provides full text search on Customers.  Searches in following parameters:   * id   * name  (optional)
	 * @param pageNumber Page number, default is 0 (optional, default to 0)
	 * @param pageSize Number of items in a page, default page size is 20, maximum 50 (optional, default to 20)
	 * @param sort Sort criteria, format: &#39;?sort&#x3D;&amp;lt;propertyA&amp;gt;[,&amp;lt;propertyB&amp;gt;][,(asc|desc)]&#39;, sort parameter can be used several times in one query  (optional)
	 * @return
	 */
	@Override
	public ResponseEntity<ListCustomersResponseDTO> getCustomers(String name, String firstName, List<UUID> customerIds, String city,
			String email, String search, Integer pageNumber, Integer pageSize, String sort) {
		ListCustomersResponseDTO listCustomersResponseDTO = customerservice.getCustomers(name, firstName, customerIds,
				city, email, search, PageRequest.of(pageNumber, pageSize, Sort.by(sort == null ? "created" : sort)));
		return ResponseEntity.ok(listCustomersResponseDTO);
	}

	@Override
	public ResponseEntity<List<CustomerDTO>> createCustomers(BulkCreationRequestDTO bulkCreationRequestDTO) {
		List<CustomerDTO> customerDTOList = customerservice.createCustomers(bulkCreationRequestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(customerDTOList);
	}

	@Override
	public ResponseEntity<Void> deleteCustomer(UUID customerId) {
		customerservice.deleteCustomer(customerId);

		return ResponseEntity.noContent().build();
	}


	@Override
	public ResponseEntity<Void> deleteCustomers(List<UUID> customersIds) {
		customerservice.deleteCustomers(customersIds);

		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<ListCustomersResponseDTO> getAllCustomers(Integer pageNumber, Integer pageSize, String sort) {
		ListCustomersResponseDTO listCustomersResponseDTO = customerservice.getAllCustomers(PageRequest.of(pageNumber, pageSize, Sort.by(sort == null ? "created" : sort)));
		return ResponseEntity.ok(listCustomersResponseDTO);
	}

	@Override
	public ResponseEntity<CustomerDTO> getCustomer(UUID customerId) {
		VersionModel<CustomerDTO> itemDTOVersionModel = customerservice.getCustomer(customerId);

		return new CustomerETagResponseEntity<>(itemDTOVersionModel).ok();

	}

	@Override
	public ResponseEntity<CustomerDTO> updateCustomer(UUID customerId, String ifMatch, UpdateCustomerRequestDTO updateCustomerRequestDTO) {
		CustomerDTO customerDTO = customerservice.updateCustomer(updateCustomerRequestDTO, customerId, ifMatch);
		return ResponseEntity.ok(customerDTO);
	}

	@Override
	public ResponseEntity<List<CustomerDTO>> updateCustomers(BulkUpdateRequestDTO bulkUpdateRequestDTO) {
		List<CustomerDTO> customerDTOList = customerservice.updateCustomers(bulkUpdateRequestDTO);
		return ResponseEntity.ok(customerDTOList);
	}

	/**
	 * This specific filtering, I have applied based on mentioned in one of the functionalities.
	 *
	 * @param customerId Customer id query parameter (optional)
	 * @param name Customer name query parameter (optional)
	 * @param firstName Customer first name query parameter (optional)
	 * @return
	 */
	@Override
	public ResponseEntity<CustomerDTO> getSingleCustomer(UUID customerId, String name, String firstName) {
		VersionModel<CustomerDTO> itemDTOVersionModel = customerservice.getSingleCustomer(customerId, name, firstName);
		return new CustomerETagResponseEntity<>(itemDTOVersionModel).ok();

	}

}