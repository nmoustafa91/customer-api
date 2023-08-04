package com.customers.db.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.customers.db.model.PhoneEntity;

@Repository
public interface PhoneRepository extends JpaRepository<PhoneEntity, UUID> {

  boolean existsByPhoneNumberAndCommunicationEntityIdNotIn(String phone, List<UUID> communicationsIds);

  boolean existsByPhoneNumber(String phone);
}
