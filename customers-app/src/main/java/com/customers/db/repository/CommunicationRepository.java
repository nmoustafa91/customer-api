package com.customers.db.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.customers.db.model.CommunicationEntity;

@Repository
public interface CommunicationRepository extends JpaRepository<CommunicationEntity, UUID> {

  boolean existsByEmailAndCustomerCustomerIdNot(String email, UUID customerId);

  boolean existsByEmail(String email);

}
