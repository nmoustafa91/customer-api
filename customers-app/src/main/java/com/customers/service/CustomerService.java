package com.customers.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.customers.db.model.VersionModel;
import com.customers.model.BulkCreationRequestDTO;
import com.customers.model.BulkUpdateRequestDTO;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.UpdateCustomerRequestDTO;

public interface CustomerService {

  VersionModel<CustomerDTO> getCustomer(UUID customerId);

  ListCustomersResponseDTO getCustomers(String name, String firstName, List<UUID> customerIds, String city,
      String email, String search, Pageable pageRequest);

  CustomerDTO createCustomer(CreateCustomerRequestDTO createCustomerRequestDTO);

  CustomerDTO updateCustomer(UpdateCustomerRequestDTO updateCustomerRequestDTO, UUID customerId, String ifMatch);

  void deleteCustomer(UUID customerId);

  ListCustomersResponseDTO getAllCustomers(PageRequest pageRequest);

  List<CustomerDTO> createCustomers(BulkCreationRequestDTO bulkCreationRequestDTO);

  void deleteCustomers(List<UUID> customersIds);

  List<CustomerDTO> updateCustomers(BulkUpdateRequestDTO bulkUpdateRequestDTO);

  VersionModel<CustomerDTO> getSingleCustomer(UUID customerId, String name, String firstName);
}
