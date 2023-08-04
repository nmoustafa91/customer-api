package com.customers.exception.general;

import lombok.Getter;

/**
 * Application domain errors.
 */
@Getter
public enum ErrorCode {


    /**
     * Item error codes (14xx)
     */
    CUSTOMER_NOT_FOUND("CUSTOMERS_ERR_1401_CUSTOMER_NOT_FOUND", "Customer with the given customer id ({}) is not found."),
    CUSTOMER_EMAIL_ALREADY_EXISTS("CUSTOMERS_ERR_1402_CUSTOMER_EMAIL_ALREADY_EXISTS", "Customer with this email or username ({}) already exists."),
    CUSTOMER_PHONE_ALREADY_EXISTS("CUSTOMERS_ERR_1403_CUSTOMER_PHONE_ALREADY_EXISTS", "Customer with this phone number ({}) already exists."),
    
    ;

    ErrorCode(final String errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    private String errorCode;
    private String message;
}
