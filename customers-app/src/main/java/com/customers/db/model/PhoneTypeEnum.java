package com.customers.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PhoneTypeEnum {

  MOBILE("MOBILE"),
  PRIVATE("PRIVATE");

  @Getter
  private String value;

  public static PhoneTypeEnum fromValue(String value) {
    for (PhoneTypeEnum o : PhoneTypeEnum.values()) {
      if (o.value.equalsIgnoreCase(value)) {
        return o;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }


}
