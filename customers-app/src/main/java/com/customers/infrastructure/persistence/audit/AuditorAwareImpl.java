package com.customers.infrastructure.persistence.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import jakarta.validation.constraints.NotNull;

public class AuditorAwareImpl implements AuditorAware<String> {

  @NotNull
  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.of(getUserId());
  }
  
  private String getUserId() {
    return "nmoustafa";
  }
}
