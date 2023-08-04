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
 * Entity represents the customer phone object in DB.
 */
@Data
@EqualsAndHashCode()
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "COMMUNICATION_PHONES")
public class PhoneEntity implements Serializable {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "COMMUNICATION_ID", nullable = false)
  private CommunicationEntity communicationEntity;

  @Column(name = "PHONE_TYPE", nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private PhoneTypeEnum phoneType = PhoneTypeEnum.MOBILE;

  @Column(name = "PHONE_NUMBER", nullable = false)
  private String phoneNumber;

}
