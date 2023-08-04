package com.customers.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This entity model is used to handle versioning representation.
 *
 * @param <T>
 */
@Data
@AllArgsConstructor
public class VersionModel<T> {

  private final Long version;

  private final T body;
}
