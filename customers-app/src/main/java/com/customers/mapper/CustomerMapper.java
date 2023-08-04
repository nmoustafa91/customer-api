package com.customers.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import com.customers.db.model.Customer;
import com.customers.db.model.VersionModel;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.PagingDTO;
import com.customers.model.UpdateCustomerRequestDTO;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper {


  List<CustomerDTO> fromEntity(List<Customer> entities);

  @Mapping(target = "lastModifiedBy", source = "updatedBy")
  @Mapping(target = "person.name", source = "name")
  @Mapping(target = "person.firstName", source = "firstName")
  @Mapping(target = "person.dateOfBirth", source = "dateOfBirth")
  CustomerDTO fromEntity(Customer entity);

  @Mapping(target = "name", source = "person.name")
  @Mapping(target = "firstName", source = "person.firstName")
  @Mapping(target = "dateOfBirth", source = "person.dateOfBirth")
  Customer fromCreateBodyToEntity(CreateCustomerRequestDTO createCustomerRequestDTO);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "name", source = "person.name")
  @Mapping(target = "firstName", source = "person.firstName")
  @Mapping(target = "dateOfBirth", source = "person.dateOfBirth")
  Customer updateEntityFromModel(UpdateCustomerRequestDTO dto,
      @MappingTarget Customer entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "name", source = "person.name")
  @Mapping(target = "firstName", source = "person.firstName")
  @Mapping(target = "dateOfBirth", source = "person.dateOfBirth")
  Customer updateEntityFromModel(CustomerDTO dto,
      @MappingTarget Customer entity);

  @AfterMapping
  default void provideCustomer(@MappingTarget Customer customer) {
    customer.getAddresses().forEach(address -> address.setCustomer(customer));
    customer.getCommunications().forEach(communication -> {
      communication.setCustomer(customer);
      communication.getPhones().forEach(phone -> phone.setCommunicationEntity(communication));
    });

  }

  default VersionModel<CustomerDTO> entityToVersionModel(Customer entity) {
    return new VersionModel<>(entity.getVersion(),
        fromEntity(entity));
  }

  default ListCustomersResponseDTO pageToCustomersResponseDTO(Page<Customer> page) {
    return new ListCustomersResponseDTO()
        .results(page.get().map(this::fromEntity).collect(Collectors.toList()))
        .paging(createPagingResponseFromPage(page));
  }

  default PagingDTO createPagingResponseFromPage(Page<?> page) {
    return new PagingDTO()
        .pageNumber(page.getNumber())
        .pageSize(page.getSize())
        .pageCount(page.getTotalPages())
        .totalElements(page.getTotalElements());
  }
}
