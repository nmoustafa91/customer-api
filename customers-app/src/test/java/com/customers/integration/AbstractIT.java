package com.customers.integration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import com.customers.CustomersApplication;
import com.customers.db.repository.CustomerRepository;
import com.customers.integration.helper.CustomersHelper;
import com.customers.model.AddressDataDTO;
import com.customers.model.AddressTypeDTO;
import com.customers.model.CommunicationDataDTO;
import com.customers.model.PersonDataDTO;
import com.customers.model.PhoneDataDTO;
import com.customers.model.PhoneTypeDTO;

@SpringBootTest(classes = CustomersApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT30S")
public class AbstractIT {

    public static final PersonDataDTO PERSON_1 = new PersonDataDTO().name("NAME_1").firstName("FIRST_NAME_1").dateOfBirth(
        LocalDate.of(1991, Month.AUGUST, 17));
    public static final PersonDataDTO PERSON_2 = new PersonDataDTO().name("NAME_2").firstName("FIRST_NAME_2").dateOfBirth(
        LocalDate.of(2001, Month.DECEMBER, 1));
    public static final PersonDataDTO PERSON_3 = new PersonDataDTO().name("NAME_3").firstName("FIRST_NAME_3").dateOfBirth(
        LocalDate.of(1995, Month.MARCH, 21));
    public static final PersonDataDTO PERSON_4 = new PersonDataDTO().name("NAME_4").firstName("FIRST_NAME_4").dateOfBirth(
        LocalDate.of(1980, Month.JANUARY, 15));
    public static final AddressDataDTO ADDRESS_1 = new AddressDataDTO().addressType(AddressTypeDTO.BILLING).street("STREET_1")
        .number("01").postal("12345").city("CITY_1");
    public static final AddressDataDTO ADDRESS_31 = new AddressDataDTO().addressType(AddressTypeDTO.DELIVERY).street("STREET_31")
        .number("01").postal("12345").city("CITY_31");
    public static final AddressDataDTO ADDRESS_32 = new AddressDataDTO().addressType(AddressTypeDTO.DELIVERY).street("STREET_32")
        .number("01").postal("12345").city("CITY_32");
    public static final AddressDataDTO ADDRESS_33 = new AddressDataDTO().addressType(AddressTypeDTO.BILLING).street("STREET_33")
        .number("01").postal("12345").city("CITY_33");
    public static final CommunicationDataDTO COMMUNICATION_11 = new CommunicationDataDTO().email("customer1_1@mail.com")
        .phones(List.of(
            new PhoneDataDTO().phoneType(PhoneTypeDTO.MOBILE).phoneNumber("1111111"),
            new PhoneDataDTO().phoneType(PhoneTypeDTO.MOBILE).phoneNumber("22222")));
    public static final CommunicationDataDTO COMMUNICATION_12 = new CommunicationDataDTO().email("customer1_2@mail.com")
        .phones(List.of());
    public static final CommunicationDataDTO COMMUNICATION_2 = new CommunicationDataDTO()
    .phones(List.of(
        new PhoneDataDTO().phoneType(PhoneTypeDTO.MOBILE).phoneNumber("1111114"),
            new PhoneDataDTO().phoneType(PhoneTypeDTO.PRIVATE).phoneNumber("222522"))).email("customer_2@mail.com");

    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    protected CustomerRepository customerRepository;



    protected CustomersHelper customersHelper;

    @BeforeEach
    protected void setUp() {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        customersHelper = new CustomersHelper(restTemplate);
    }

}
