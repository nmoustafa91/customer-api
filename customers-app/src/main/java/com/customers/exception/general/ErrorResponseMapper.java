package com.customers.exception.general;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import com.customers.model.ApiErrorResponseDTO;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ErrorResponseMapper {

  ApiErrorResponseDTO errorToDTO(ApplicationError error);
}
