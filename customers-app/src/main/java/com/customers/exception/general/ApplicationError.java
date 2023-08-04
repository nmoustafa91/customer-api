package com.customers.exception.general;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.message.ParameterizedMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Application error that occurred in runtime.
 */
@Data
@Accessors(chain = true)
public class ApplicationError implements Serializable {

  private String traceId = UUID.randomUUID().toString();
  private int status;
  @Setter(AccessLevel.NONE)
  private String error;
  @Setter(AccessLevel.NONE)
  private String message;
  private String code;
  private OffsetDateTime timestamp = OffsetDateTime.now();
  private String path;
  private List<String> details;
  @JsonIgnore
  @ToString.Exclude
  @Setter(AccessLevel.PRIVATE)
  private String rawMessage;
  @JsonIgnore
  @ToString.Exclude
  private List<Object> parameters = new LinkedList<>();

  public ApplicationError() {
  }

  public String getMessage() {
    if (!CollectionUtils.isEmpty(getParameters())) {
      return ParameterizedMessage.format(getRawMessage(),
          getParameters().toArray());
    } else {
      return getRawMessage();
    }
  }

  public ApplicationError setCodeAndMessage(ErrorCode errorDataProvider) {
    setCode(errorDataProvider.getErrorCode());
    setRawMessage(errorDataProvider.getMessage());
    return this;
  }

  public void setHttpStatus(HttpStatusCode status) {
    this.status = status.value();
    HttpStatus httpStatus = HttpStatus.resolve(status.value());
    this.error = httpStatus != null ? httpStatus.getReasonPhrase() : null;
  }

}
