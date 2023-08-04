package com.customers.db.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.customers.db.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>,
    PagingAndSortingRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

  Optional<Customer> findOneByCustomerIdAndNameAndFirstName(UUID customerId, String name, String firstName);

  List<Customer> findAllByCustomerIdIn(List<UUID> customerIds);

  boolean existsByCustomerIdIn(List<UUID> customerIds);
}
