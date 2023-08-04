package com.customers.db.model;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode()
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class UserAwareEntity {

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  @Column(name = "LAST_MODIFIED_BY")
  private String updatedBy;

  @CreatedDate
  private OffsetDateTime created;

  @LastModifiedDate
  private OffsetDateTime lastModified;

}
