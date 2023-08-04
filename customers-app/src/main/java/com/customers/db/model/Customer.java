package com.customers.db.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Entity represents the customer object in DB.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "customer")
public class Customer
    extends UserAwareEntity implements Serializable {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID customerId;

  @Version
  private Long version;

  @Column(name = "NAME", nullable = false)
  private String name;

  @Column(name = "FIRST_NAME", nullable = false)
  private String firstName;

  @Column(name = "DATE_OF_BIRTH")
  private LocalDate dateOfBirth;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Builder.Default
  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
  private Set<Address> addresses = new HashSet<>();

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Builder.Default
  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
  private Set<CommunicationEntity> communications = new HashSet<>();

}
