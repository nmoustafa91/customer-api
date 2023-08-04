package com.customers.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AddressTypeEnum {

  BILLING("BILLING"),
  DELIVERY("DELIVERY");

  @Getter
  private String value;

  public static AddressTypeEnum fromValue(String value) {
    for (AddressTypeEnum o : AddressTypeEnum.values()) {
      if (o.value.equalsIgnoreCase(value)) {
        return o;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }


}
