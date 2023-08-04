package com.customers.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.customers.db.model.CommunicationEntity;
import com.customers.db.model.Customer;
import com.customers.db.model.VersionModel;
import com.customers.db.repository.CommunicationRepository;
import com.customers.db.repository.CustomerRepository;
import com.customers.db.repository.CustomerSpecificationHelper;
import com.customers.db.repository.PhoneRepository;
import com.customers.domain.CustomerFilter;
import com.customers.etag.utils.ETagUtils;
import com.customers.exception.NotFoundException;
import com.customers.exception.ValidationException;
import com.customers.exception.general.ApplicationError;
import com.customers.exception.general.ErrorCode;
import com.customers.mapper.CustomerMapper;
import com.customers.model.BulkCreationRequestDTO;
import com.customers.model.BulkUpdateRequestDTO;
import com.customers.model.CommunicationDataDTO;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.PhoneDataDTO;
import com.customers.model.UpdateCustomerRequestDTO;
import com.customers.service.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CommunicationRepository communicationRepository;
  private final PhoneRepository phoneRepository;
  private final CustomerMapper customerMapper;

  @Override
  public VersionModel<CustomerDTO> getCustomer(UUID customerId) {
    Customer customer = getCustomerById(customerId);

    return customerMapper.entityToVersionModel(customer);
  }

  @Override
  public ListCustomersResponseDTO getCustomers(String name, String firstName, List<UUID> customerIds, String city,
      String email, String search, Pageable pageRequest) {
    final CustomerFilter filter = CustomerFilter.builder().name(name).firstName(firstName)
        .city(city).search(search).email(email)
        .customersIds(customerIds).build();

    Page<Customer> page = customerRepository.findAll(CustomerSpecificationHelper.createFilter(filter), pageRequest);
    return customerMapper.pageToCustomersResponseDTO(page);
  }

  @Override
  public CustomerDTO createCustomer(CreateCustomerRequestDTO createCustomerRequestDTO) {
    Customer customer = customerMapper.fromCreateBodyToEntity(createCustomerRequestDTO);

    validateCustomerCreate(createCustomerRequestDTO);

    customerMapper.provideCustomer(customer);
    return customerMapper.fromEntity(customerRepository.save(customer));
  }

  @Override
  public CustomerDTO updateCustomer(UpdateCustomerRequestDTO updateCustomerRequestDTO, UUID customerId,
      String ifMatch) {
    Customer customer = getCustomerById(customerId);

    ETagUtils.checkETag(customer, ifMatch);
    validateCustomerUpdate(updateCustomerRequestDTO.getCommunications(), customerId, customer);

    customer = customerMapper.updateEntityFromModel(updateCustomerRequestDTO, customer);

    return customerMapper.fromEntity(customerRepository.save(customer));
  }

  @Override
  public void deleteCustomer(UUID customerId) {
    Customer customer = getCustomerById(customerId);
    customerRepository.delete(customer);
  }

  @Override
  public ListCustomersResponseDTO getAllCustomers(PageRequest pageRequest) {
    Page<Customer> page = customerRepository.findAll(pageRequest);
    return customerMapper.pageToCustomersResponseDTO(page);
  }

  @Override
  public List<CustomerDTO> createCustomers(BulkCreationRequestDTO bulkCreationRequestDTO) {
    List<Customer> customerEntities = bulkCreationRequestDTO.getCustomers().stream()
        .map(currentRequest -> {
          validateCustomerCreate(currentRequest);
          Customer customer = customerMapper.fromCreateBodyToEntity(currentRequest);
          customerMapper.provideCustomer(customer);
          return customer;
        }).toList();
    return customerMapper.fromEntity(customerRepository.saveAll(customerEntities));
  }

  @Override
  public void deleteCustomers(List<UUID> customersIds) {
    checkForCustomersExistence(customersIds);
    List<Customer> customers = customerRepository.findAllByCustomerIdIn(customersIds);
    customers.forEach(customerRepository::delete);
  }

  @Override
  public List<CustomerDTO> updateCustomers(BulkUpdateRequestDTO bulkUpdateRequestDTO) {
    checkForCustomersExistence(bulkUpdateRequestDTO.getCustomers().stream().map(CustomerDTO::getCustomerId).toList());
    final List<Customer> customerEntities = bulkUpdateRequestDTO.getCustomers().stream().map(customerRequest -> {
      final UUID customerId = customerRequest.getCustomerId();
      Customer customer = getCustomerById(customerId);
      validateCustomerUpdate(customerRequest.getCommunications(), customerId, customer);
      return customerMapper.updateEntityFromModel(customerRequest, customer);
    }).toList();
    return customerMapper.fromEntity(customerRepository.saveAll(customerEntities));
  }

  @Override
  public VersionModel<CustomerDTO> getSingleCustomer(UUID customerId, String name, String firstName) {
    Customer customer = customerRepository.findOneByCustomerIdAndNameAndFirstName(customerId, name, firstName).orElseThrow(
        () -> new NotFoundException(new ApplicationError()
            .setParameters(List.of(customerId))
            .setCodeAndMessage(ErrorCode.CUSTOMER_NOT_FOUND)));

    return customerMapper.entityToVersionModel(customer);
  }

  private void checkForCustomersExistence(List<UUID> customersIds) {
    Optional<UUID> noneExistingCustomer = customersIds.stream().filter(customerId -> !customerRepository.existsById(customerId))
        .findFirst();
    if (noneExistingCustomer.isPresent()) {
      throw new ValidationException(new ApplicationError()
          .setParameters(List.of(noneExistingCustomer.get()))
          .setCodeAndMessage(ErrorCode.CUSTOMER_NOT_FOUND));
    }
  }

  private void validateCustomerUpdate(List<CommunicationDataDTO> communications, UUID customerId,
      Customer customer) {
    if (communications == null || communications.isEmpty()) {
      return;
    }
    final Optional<String> duplicatedEmailOptional = communications.stream().map(CommunicationDataDTO::getEmail)
        .filter(
            email -> communicationRepository
                .existsByEmailAndCustomerCustomerIdNot(email, customerId)).findFirst();
    if (duplicatedEmailOptional.isPresent()) {
      throw new ValidationException(new ApplicationError()
          .setParameters(List.of(duplicatedEmailOptional.get()))
          .setCodeAndMessage(ErrorCode.CUSTOMER_EMAIL_ALREADY_EXISTS));
    }
    List<UUID> communicationsIds = customer.getCommunications().stream().map(CommunicationEntity::getId).toList();
    final List<String> phones = communications.stream()
        .map(CommunicationDataDTO::getPhones).flatMap(List::stream).map(PhoneDataDTO::getPhoneNumber).toList();
    final Optional<String> duplicatedPhoneOptional = phones.stream()
        .filter(
            phone -> phoneRepository
                .existsByPhoneNumberAndCommunicationEntityIdNotIn(phone, communicationsIds)).findFirst();
    if (duplicatedPhoneOptional.isPresent()) {
      throw new ValidationException(new ApplicationError()
          .setParameters(List.of(duplicatedPhoneOptional.get()))
          .setCodeAndMessage(ErrorCode.CUSTOMER_PHONE_ALREADY_EXISTS));
    }
  }

  private void validateCustomerCreate(CreateCustomerRequestDTO createCustomerRequestDTO) {
    if (createCustomerRequestDTO.getCommunications() == null || createCustomerRequestDTO.getCommunications().isEmpty()) {
      return;
    }
    final Optional<String> duplicatedEmailOptional = createCustomerRequestDTO.getCommunications().stream().map(CommunicationDataDTO::getEmail)
        .filter(communicationRepository::existsByEmail).findFirst();
    if (duplicatedEmailOptional.isPresent()) {
      throw new ValidationException(new ApplicationError()
          .setParameters(List.of(duplicatedEmailOptional.get()))
          .setCodeAndMessage(ErrorCode.CUSTOMER_EMAIL_ALREADY_EXISTS));
    }

    final List<String> phones = createCustomerRequestDTO.getCommunications().stream()
        .map(CommunicationDataDTO::getPhones).flatMap(List::stream).map(PhoneDataDTO::getPhoneNumber).toList();
    final Optional<String> duplicatedPhoneOptional = phones.stream()
        .filter(phoneRepository::existsByPhoneNumber).findFirst();
    if (duplicatedPhoneOptional.isPresent()) {
      throw new ValidationException(new ApplicationError()
          .setParameters(List.of(duplicatedPhoneOptional.get()))
          .setCodeAndMessage(ErrorCode.CUSTOMER_PHONE_ALREADY_EXISTS));
    }
  }

  private Customer getCustomerById(UUID customerId) {
    return customerRepository.findById(customerId).orElseThrow(
        () -> new NotFoundException(new ApplicationError()
            .setParameters(List.of(customerId))
            .setCodeAndMessage(ErrorCode.CUSTOMER_NOT_FOUND)));
  }
}
