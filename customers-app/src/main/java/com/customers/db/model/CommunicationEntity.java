package com.customers.db.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Entity represents the customer communication object in DB.
 */
@Data
@EqualsAndHashCode()
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "CUSTOMER_COMMUNICATIONS")
public class CommunicationEntity implements Serializable {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "CUSTOMER_ID", nullable = false)
  private Customer customer;

  @Column(name = "EMAIL", nullable = false)
  private String email;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Builder.Default
  @OneToMany(mappedBy = "communicationEntity", cascade = CascadeType.ALL)
  private Set<PhoneEntity> phones = new HashSet<>();

}
