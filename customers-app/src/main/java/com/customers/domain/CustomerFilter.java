package com.customers.domain;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CustomerFilter {

  private String name;
  private String firstName;
  private String city;
  private String email;
  private String search;
  private List<UUID> customersIds;
}
