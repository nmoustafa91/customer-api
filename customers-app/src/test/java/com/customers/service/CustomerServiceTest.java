package com.customers.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.customers.db.model.Customer;
import com.customers.db.model.VersionModel;
import com.customers.db.repository.CustomerRepository;
import com.customers.exception.NotFoundException;
import com.customers.mapper.CustomerMapper;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.PagingDTO;
import com.customers.model.PersonDataDTO;
import com.customers.model.UpdateCustomerRequestDTO;
import com.customers.service.impl.CustomerServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

  public static final String CUSTOMER_NAME = "name";
  public static final String CUSTOMER_FIRST_NAME = "firstName";
  public static final LocalDate CUSTOMER_FIRST_DATE_OF_BIRTH = LocalDate.of(1990, Month.AUGUST, 5);
  public static final UUID CUSTOMER_ID = UUID.randomUUID();
  public static final CustomerDTO CUSTOMER_DTO = new CustomerDTO().customerId(CUSTOMER_ID).person(
      new PersonDataDTO().name(CUSTOMER_NAME).firstName(CUSTOMER_FIRST_NAME).dateOfBirth(CUSTOMER_FIRST_DATE_OF_BIRTH));
  private static final Customer CUSTOMER_ENTITY = Customer.builder().customerId(CUSTOMER_ID).name(
      CUSTOMER_NAME).firstName(CUSTOMER_FIRST_NAME).dateOfBirth(CUSTOMER_FIRST_DATE_OF_BIRTH).version(1L).build();

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CustomerMapper customerMapper;

  @InjectMocks
  private CustomerServiceImpl customerService;

  @Test
  void createCustomer() {
    CreateCustomerRequestDTO customerRequest = new CreateCustomerRequestDTO().person(
        new PersonDataDTO().name(CUSTOMER_NAME).firstName(CUSTOMER_FIRST_NAME).dateOfBirth(CUSTOMER_FIRST_DATE_OF_BIRTH));
    when(customerMapper.fromCreateBodyToEntity(any())).thenReturn(CUSTOMER_ENTITY);
    customerService.createCustomer(customerRequest);

    verify(customerRepository).save(any(Customer.class));
  }

  @Test
  void createCustomer_checkCustomerProperties() {
    CreateCustomerRequestDTO customerRequest = new CreateCustomerRequestDTO().person(
        new PersonDataDTO().name(CUSTOMER_NAME).firstName(CUSTOMER_FIRST_NAME).dateOfBirth(CUSTOMER_FIRST_DATE_OF_BIRTH))
        .addresses(List.of())
        .communications(List.of());
    when(customerMapper.fromCreateBodyToEntity(any())).thenReturn(CUSTOMER_ENTITY);
    when(customerMapper.fromEntity(any(Customer.class))).thenReturn(CUSTOMER_DTO);

    mockSaveCustomer();

    CustomerDTO customerDTO = customerService.createCustomer(customerRequest);

    assertNotNull(customerDTO);
    assertEquals(customerDTO.getCustomerId(), CUSTOMER_ID);
    assertEquals(customerDTO.getPerson().getName(), CUSTOMER_NAME);
    assertEquals(customerDTO.getPerson().getFirstName(), CUSTOMER_FIRST_NAME);
    assertEquals(customerDTO.getPerson().getDateOfBirth(), CUSTOMER_FIRST_DATE_OF_BIRTH);
  }

  @Test
  void getCustomer() {
    when(customerRepository.findById(any())).thenReturn(Optional.ofNullable(CUSTOMER_ENTITY));
    when(customerMapper.entityToVersionModel(any())).thenReturn(new VersionModel<>(1L, CUSTOMER_DTO));

    assertDoesNotThrow(() -> customerService.getCustomer(CUSTOMER_ID));

    VersionModel<CustomerDTO> customerDTOVersionModel = customerService.getCustomer(CUSTOMER_ID);
    assertNotNull(customerDTOVersionModel);
    assertEquals(customerDTOVersionModel.getVersion(), 1L);
    final var body = customerDTOVersionModel.getBody();
    assertNotNull(body);
    assertEquals(body.getCustomerId(), CUSTOMER_ID);
    assertEquals(body.getPerson().getName(), CUSTOMER_NAME);
    assertEquals(body.getPerson().getFirstName(), CUSTOMER_FIRST_NAME);
    assertEquals(body.getPerson().getDateOfBirth(), CUSTOMER_FIRST_DATE_OF_BIRTH);
  }

  @Test
  void getCustomer_throwsNotFoundException() {
    when(customerRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> customerService.getCustomer(CUSTOMER_ID));
  }

  @Test
  void filterCustomers() {
    Page<Customer> page = new PageImpl<>(List.of(CUSTOMER_ENTITY));
    when(customerRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(page);
    when(customerMapper.pageToCustomersResponseDTO(eq(page)))
        .thenReturn(new ListCustomersResponseDTO()
            .results(List.of(new CustomerDTO().customerId(CUSTOMER_ID).person(
                new PersonDataDTO().name(CUSTOMER_NAME).firstName(CUSTOMER_FIRST_NAME).dateOfBirth(CUSTOMER_FIRST_DATE_OF_BIRTH))
                .addresses(List.of())
                .communications(List.of())))
            .paging(new PagingDTO().pageNumber(1).pageSize(20)));

    ListCustomersResponseDTO customersResponseDTO = customerService.getCustomers(CUSTOMER_NAME, null, List.of(CUSTOMER_ID), null, null, null,
        PageRequest.of(0, 20));
    assertNotNull(customersResponseDTO);
    assertNotNull(customersResponseDTO.getResults());
    assertNotNull(customersResponseDTO.getPaging());
  }

  @Test
  void updateCustomer() {
    UpdateCustomerRequestDTO updateCustomerRequestDTO = new UpdateCustomerRequestDTO().person(
        new PersonDataDTO().name(CUSTOMER_NAME).firstName(CUSTOMER_FIRST_NAME).dateOfBirth(CUSTOMER_FIRST_DATE_OF_BIRTH))
        .addresses(List.of())
        .communications(List.of());
    when(customerRepository.findById(any())).thenReturn(Optional.ofNullable(CUSTOMER_ENTITY));
    when(customerMapper.updateEntityFromModel(eq(updateCustomerRequestDTO), any()))
        .thenReturn(CUSTOMER_ENTITY);

    customerService.updateCustomer(updateCustomerRequestDTO, CUSTOMER_ID, "\"1\"");
    verify(customerRepository).save(any(Customer.class));
  }

  @Test
  void deleteCustomer() {
    when(customerRepository.findById(any())).thenReturn(Optional.ofNullable(CUSTOMER_ENTITY));

    customerService.deleteCustomer(CUSTOMER_ID);
    verify(customerRepository).delete(any(Customer.class));
  }

  private void mockSaveCustomer() {
    doAnswer(answer((Customer customer) -> customer))
        .when(customerRepository).save(any(Customer.class));
  }

}
