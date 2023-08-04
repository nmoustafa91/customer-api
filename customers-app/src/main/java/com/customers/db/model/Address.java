package com.customers.db.model;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Entity represents the customer address object in DB.
 */
@Data
@EqualsAndHashCode()
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "address")
public class Address implements Serializable {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "CUSTOMER_ID", nullable = false)
  private Customer customer;

  @Column(name = "ADDRESS_TYPE", nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private AddressTypeEnum addressType = AddressTypeEnum.BILLING;

  @Column(name = "STREET", nullable = false)
  private String street;

  @Column(name = "NUMBER")
  private String number;

  @Column(name = "POSTAL", nullable = false)
  private String postal;

  @Column(name = "CITY", nullable = false)
  private String city;

}
