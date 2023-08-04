package com.customers.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.customers.model.AddressDataDTO;
import com.customers.model.AddressTypeDTO;
import com.customers.model.BulkCreationRequestDTO;
import com.customers.model.BulkUpdateRequestDTO;
import com.customers.model.CommunicationDataDTO;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.PersonDataDTO;
import com.customers.model.PhoneDataDTO;
import com.customers.model.PhoneTypeDTO;
import com.customers.model.UpdateCustomerRequestDTO;

public class CustomersApiControllerIT extends AbstractIT {

	@BeforeEach
	void init() {
		customerRepository.deleteAll();
	}

	@Test
	void createCustomer() {
		var request = new CreateCustomerRequestDTO().person(PERSON_1).communications(
				List.of(COMMUNICATION_11, COMMUNICATION_12)
		).addresses(List.of(ADDRESS_1));
		var response = customersHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var customerDTO = response.getBody();
		assertEquals(PERSON_1.getName(), customerDTO.getPerson().getName());
		assertEquals(PERSON_1.getFirstName(), customerDTO.getPerson().getFirstName());
		assertEquals(PERSON_1.getDateOfBirth(), customerDTO.getPerson().getDateOfBirth());
		assertNotNull(customerDTO.getCreated());
		assertNotNull(customerDTO.getCreatedBy());
		assertThat(customerDTO.getAddresses().size(), is(1));
		assertEquals(ADDRESS_1, customerDTO.getAddresses().get(0));
		assertThat(customerDTO.getCommunications().size(), is(2));
		assertThat(customerDTO.getCommunications(), containsInAnyOrder(COMMUNICATION_11, COMMUNICATION_12));
	}

	@Test
	void createEmptyCustomer() {
		var request = new CreateCustomerRequestDTO().person(PERSON_4).communications(
				List.of()
		).addresses(List.of());
		var response = customersHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var customerDTO = response.getBody();
		assertEquals(PERSON_4.getName(), customerDTO.getPerson().getName());
		assertEquals(PERSON_4.getFirstName(), customerDTO.getPerson().getFirstName());
		assertEquals(PERSON_4.getDateOfBirth(), customerDTO.getPerson().getDateOfBirth());
		assertNotNull(customerDTO.getCreated());
		assertNotNull(customerDTO.getCreatedBy());
		assertThat(customerDTO.getAddresses().size(), is(0));
		assertThat(customerDTO.getCommunications().size(), is(0));
	}

	@Test
	void createCustomer_withAnExistingEmail_shouldFail() {
		var request = new CreateCustomerRequestDTO().person(PERSON_1).communications(
				List.of(COMMUNICATION_11, COMMUNICATION_12)
		).addresses(List.of(ADDRESS_1));
		var response = customersHelper.create(request);

		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

		request = new CreateCustomerRequestDTO().person(PERSON_2).communications(
				List.of(COMMUNICATION_11)
		).addresses(List.of(ADDRESS_31));
		response = customersHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	void updateCustomer_notFound() {
		var request = new UpdateCustomerRequestDTO().person(new PersonDataDTO().name("updated"));
		customersHelper.update(request, UUID.randomUUID(), HttpStatus.NOT_FOUND);
	}

	@Test
	void updateCustomer_withExistingCustomerCommunication_shouldFail() {
		var request = new CreateCustomerRequestDTO().person(PERSON_1).communications(
				List.of(COMMUNICATION_11, COMMUNICATION_12)
		).addresses(List.of(ADDRESS_1));
		customersHelper.create(request);
		request = new CreateCustomerRequestDTO().person(PERSON_4).communications(
				List.of()
		).addresses(List.of());
		var response = customersHelper.create(request);
		var customer = response.getBody();
		var updateRequest = new UpdateCustomerRequestDTO().communications(List.of(COMMUNICATION_12));
		customersHelper.update(updateRequest, customer.getCustomerId(), HttpStatus.BAD_REQUEST);
	}

	@Test
	void updateCustomer() {
		var request = new CreateCustomerRequestDTO().person(PERSON_1).communications(
				List.of(COMMUNICATION_11, COMMUNICATION_12)
		).addresses(List.of(ADDRESS_1));
		var response = customersHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var customerDTO = response.getBody();
		final AddressDataDTO newAddress = new AddressDataDTO().addressType(AddressTypeDTO.BILLING).street("Gadebuscher")
				.city("Berlin").number("25").postal("12619");
		var updateRequest = new UpdateCustomerRequestDTO().person(new PersonDataDTO().name("updated"))
				.addresses(List.of(newAddress));
		response = customersHelper.update(updateRequest, customerDTO.getCustomerId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		var updatedCustomer = response.getBody();
		assertThat(updatedCustomer.getPerson().getName(), is("updated"));
		assertThat(updatedCustomer.getAddresses(), hasItem(newAddress));
		assertNotNull(updatedCustomer.getCreated());
		assertNotNull(updatedCustomer.getCreatedBy());
		assertNotNull(updatedCustomer.getLastModified());
		assertNotNull(updatedCustomer.getLastModifiedBy());
	}



	@Test
	void updateCustomer_REMOVEaDDRESS() {
		var request = new CreateCustomerRequestDTO().person(PERSON_3).communications(List.of())
				.addresses(List.of(ADDRESS_31, ADDRESS_32, ADDRESS_33));
		var response = customersHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var customerDTO = response.getBody();
		assertThat(customerDTO.getPerson(), is(PERSON_3));
		assertFalse(customerDTO.getAddresses().isEmpty());

		var updateRequest = new UpdateCustomerRequestDTO().addresses(List.of());
		response = customersHelper.update(updateRequest, customerDTO.getCustomerId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		var updatedCustomer = response.getBody();
		assertThat(updatedCustomer.getPerson().getName(), is(PERSON_3.getName()));
		assertTrue(updatedCustomer.getAddresses().isEmpty());
		assertNotNull(updatedCustomer.getCreated());
		assertNotNull(updatedCustomer.getCreatedBy());
	}

	@Test
	void getCustomer_notFound() {
		customersHelper.getCustomer(UUID.randomUUID(), HttpStatus.NOT_FOUND);
	}

	@Test
	void getCustomer() {
		var request = new CreateCustomerRequestDTO().person(PERSON_1).communications(
				List.of(COMMUNICATION_11, COMMUNICATION_12)
		).addresses(List.of(ADDRESS_1));
		var response = customersHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var customerDTO = response.getBody();
		response = customersHelper.getCustomer(customerDTO.getCustomerId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		var getCustomerResponse = response.getBody();
		assertEquals(PERSON_1.getName(), getCustomerResponse.getPerson().getName());
		assertEquals(PERSON_1.getFirstName(), getCustomerResponse.getPerson().getFirstName());
		assertEquals(PERSON_1.getDateOfBirth(), getCustomerResponse.getPerson().getDateOfBirth());
		assertNotNull(getCustomerResponse.getCreated());
		assertNotNull(getCustomerResponse.getCreatedBy());
		assertThat(getCustomerResponse.getAddresses().size(), is(1));
		assertEquals(ADDRESS_1, getCustomerResponse.getAddresses().get(0));
		assertThat(getCustomerResponse.getCommunications().size(), is(2));

	}

	@Test
	void deleteCustomer() {
		var request = new CreateCustomerRequestDTO().person(PERSON_3).communications(List.of())
				.addresses(List.of(ADDRESS_31, ADDRESS_32, ADDRESS_33));
		var response = customersHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var customerDTO = response.getBody();
		customersHelper.deleteCustomer(customerDTO.getCustomerId());
		assertFalse(customerRepository.existsById(customerDTO.getCustomerId()));
	}

	@Test
	void listCustomers_Unfiltered() {
		prepareTestData();

		var response = customersHelper.getCustomers(null, null, null,
				null, null, null, null);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(4, response.getBody().getResults().size());
	}

	@Test
	void listCustomers_filteredByName() {
		prepareTestData();

		var response = customersHelper.getCustomers("NAME", null, null,
				null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(4, response.getBody().getResults().size());
		response = customersHelper.getCustomers("NAME_1", null, null,
				null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(1, response.getBody().getResults().size());
		response = customersHelper.getCustomers("MARTIN", null, null,
				null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());
	}

	@Test
	void listCustomers_filteredByCustomerCity() {
		prepareTestData();

		var response = customersHelper.getCustomers(null, null, null,
				null, "CITY", null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());
		response = customersHelper.getCustomers(null, null, null,
				null, ADDRESS_1.getCity(), null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(2, response.getBody().getResults().size());
		response = customersHelper.getCustomers("", null, null,
				null, ADDRESS_31.getCity(), null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(1, response.getBody().getResults().size());
	}

	@Test
	void listCustomers_filteredByCustomerEmail() {
		prepareTestData();

		var response = customersHelper.getCustomers(null, null, null,
				COMMUNICATION_12.getEmail(), null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(1, response.getBody().getResults().size());
	}
	
	@Test
	void listCustomers_filterByCustomerIds() {
		final List<String> customerIds = prepareTestData();

		var response = customersHelper.getCustomers(null, null, customerIds,
				null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(4, response.getBody().getResults().size());

		response = customersHelper.getCustomers(null, null, List.of(customerIds.get(0), customerIds.get(2)),
				null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(2, response.getBody().getResults().size());

		response = customersHelper.getCustomers(null, null, List.of(UUID.randomUUID().toString()),
				null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());
	}

	@Test
	void listCustomers_filterBySearch() {
		final List<String> customerIds = prepareTestData();

		var response = customersHelper.getCustomers(null, null, null,
				null, null, "NAME", PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(4, response.getBody().getResults().size());

		response = customersHelper.getCustomers(null, null, null,
				null, null, customerIds.get(0), PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(1, response.getBody().getResults().size());

		response = customersHelper.getCustomers(null, null, null,
				null, null, "xyz", PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());
	}

	@Test
	void getAllCustomers() {
		prepareTestData();

		var response = customersHelper.getAllCustomers(PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(4, response.getBody().getResults().size());
	}

	@Test
	void filterSingleRecord() {
		var request = new CreateCustomerRequestDTO().person(PERSON_1).communications(
				List.of(COMMUNICATION_11, COMMUNICATION_12)
		).addresses(List.of(ADDRESS_1));

		var customer = customersHelper.create(request).getBody();
		var response = customersHelper.getSingleFilteredCustomer(PERSON_1.getName(), PERSON_1.getFirstName(),
				customer.getCustomerId().toString());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(customer.getCustomerId(), response.getBody().getCustomerId());
	}

	@Test
	void createBulkOfCustomers() {
		var request1 = new CreateCustomerRequestDTO().person(PERSON_1).communications(
				List.of(COMMUNICATION_11, COMMUNICATION_12)
		).addresses(List.of(ADDRESS_1));
		var request2 = new CreateCustomerRequestDTO().person(PERSON_2).communications(
				List.of(COMMUNICATION_2)
		).addresses(List.of(ADDRESS_1));
		var request3 = new CreateCustomerRequestDTO().person(PERSON_3)
				.addresses(List.of(ADDRESS_31, ADDRESS_32, ADDRESS_33))
				.communications(List.of());
		var request4 = new CreateCustomerRequestDTO().person(PERSON_4).communications(
				List.of()).addresses(List.of());
		BulkCreationRequestDTO bulkCreationRequestDTO = new BulkCreationRequestDTO().customers(List.of(
				request1, request2, request3, request4
		));

		var response = customersHelper.createBulk(bulkCreationRequestDTO);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var listOfCreatedCustomers = response.getBody();
		assertThat(listOfCreatedCustomers.size(), is(4));
	}

	@Test
	void updateBulkOfCustomers() {
		List<CustomerDTO> customers = extractPreparedData();

		customers.get(0).setPerson(new PersonDataDTO().name("updated").firstName("first_updated").dateOfBirth(LocalDate.of(2001, Month.JANUARY,
				5)));
		customers.get(1).getAddresses().add(ADDRESS_1);
		customers.get(2).setAddresses(List.of());
		customers.get(2).getCommunications().add(new CommunicationDataDTO().email("test123@email.com").phones(List.of(
				new PhoneDataDTO().phoneType(PhoneTypeDTO.MOBILE).phoneNumber("5555555555")
		)));
		customers.get(3).getCommunications().add(new CommunicationDataDTO().email("test@email.com").phones(List.of(
				new PhoneDataDTO().phoneType(PhoneTypeDTO.MOBILE).phoneNumber("5555555544444455")
		)));
		customers.get(3).getAddresses().add(new AddressDataDTO().addressType(AddressTypeDTO.DELIVERY).city("city").street("street")
				.number("25").postal("123"));

		BulkUpdateRequestDTO bulkUpdateRequestDTO = new BulkUpdateRequestDTO().customers(customers);

		var response = customersHelper.updateBulk(bulkUpdateRequestDTO);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		var listOfCreatedCustomers = response.getBody();
		assertThat(listOfCreatedCustomers.size(), is(4));
	}

	@Test
	void deleteBulkOfCustomers() {
		List<UUID> customerIds = extractPreparedData().stream().map(CustomerDTO::getCustomerId).toList();

		var response = customersHelper.deleteCustomersBulk(customerIds);
		assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
		assertFalse(customerRepository.existsByCustomerIdIn(customerIds));
	}

	private List<CustomerDTO> extractPreparedData() {
		var request1 = new CreateCustomerRequestDTO().person(PERSON_1).communications(
				List.of(COMMUNICATION_11, COMMUNICATION_12)
		).addresses(List.of(ADDRESS_1));
		var request2 = new CreateCustomerRequestDTO().person(PERSON_2).communications(
				List.of(COMMUNICATION_2)
		).addresses(List.of(ADDRESS_1));
		var request3 = new CreateCustomerRequestDTO().person(PERSON_3)
				.addresses(List.of(ADDRESS_31, ADDRESS_32, ADDRESS_33))
				.communications(List.of());
		var request4 = new CreateCustomerRequestDTO().person(PERSON_4).communications(
				List.of()).addresses(List.of());

		var customer1 = customersHelper.create(request1).getBody();
		var customer2 = customersHelper.create(request2).getBody();
		var customer3 = customersHelper.create(request3).getBody();
		var customer4 = customersHelper.create(request4).getBody();

		return List.of(customer1, customer2, customer3, customer4);
	}

	private List<String> prepareTestData() {
		var request1 = new CreateCustomerRequestDTO().person(PERSON_1).communications(
				List.of(COMMUNICATION_11, COMMUNICATION_12)
		).addresses(List.of(ADDRESS_1));
		var request2 = new CreateCustomerRequestDTO().person(PERSON_2).communications(
				List.of(COMMUNICATION_2)
		).addresses(List.of(ADDRESS_1));
		var request3 = new CreateCustomerRequestDTO().person(PERSON_3)
				.addresses(List.of(ADDRESS_31, ADDRESS_32, ADDRESS_33))
				.communications(List.of());
		var request4 = new CreateCustomerRequestDTO().person(PERSON_4).communications(
				List.of()).addresses(List.of());

		var customer1 = customersHelper.create(request1).getBody();
		var customer2 = customersHelper.create(request2).getBody();
		var customer3 = customersHelper.create(request3).getBody();
		var customer4 = customersHelper.create(request4).getBody();

		return List.of(customer1.getCustomerId().toString(), customer2.getCustomerId().toString(),
				customer3.getCustomerId().toString(), customer4.getCustomerId().toString());
	}
}
