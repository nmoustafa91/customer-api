package com.customers.db.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.customers.db.model.Address;
import com.customers.db.model.Address_;
import com.customers.db.model.CommunicationEntity;
import com.customers.db.model.CommunicationEntity_;
import com.customers.db.model.Customer;
import com.customers.db.model.Customer_;
import com.customers.domain.CustomerFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerSpecificationHelper {

	public Specification<Customer> createFilter(CustomerFilter customerFilter) {
		return (root, query, cb) -> {
			final List<Predicate> predicates = new LinkedList<>();

			// name
			if (StringUtils.hasText(customerFilter.getName())) {
				predicates.add(cb.like(cb.lower(root.get(Customer_.NAME)),
						"%" + customerFilter.getName().toLowerCase(Locale.ROOT) + "%"));
			}

			// first name
			if (StringUtils.hasText(customerFilter.getFirstName())) {
				predicates.add(cb.like(cb.lower(root.get(Customer_.FIRST_NAME)),
						"%" + customerFilter.getFirstName().toLowerCase(Locale.ROOT) + "%"));
			}

			// city
			if (StringUtils.hasText(customerFilter.getCity())) {
				Join<Customer, Address> addressJoin = root.join(Customer_.ADDRESSES);
				predicates.add(cb.equal(addressJoin.get(Address_.CITY), customerFilter.getCity()));
			}

			// email
			if (StringUtils.hasText(customerFilter.getEmail())) {
				Join<Customer, CommunicationEntity> communicationJoin = root.join(Customer_.COMMUNICATIONS);
				predicates.add(cb.equal(communicationJoin.get(CommunicationEntity_.EMAIL), customerFilter.getEmail()));
			}

			// ids
			if (!CollectionUtils.isEmpty(customerFilter.getCustomersIds())) {
				predicates.add(root.get(Customer_.CUSTOMER_ID).in(customerFilter.getCustomersIds()));
			}

			// Search
			if (StringUtils.hasText(customerFilter.getSearch())) {
				predicates.add(cb.or(cb.equal(root.get(Customer_.CUSTOMER_ID).as(String.class), customerFilter.getSearch()),
						lowercaseLike(cb, root.get(Customer_.NAME), customerFilter.getSearch())));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Predicate lowercaseLike(final CriteriaBuilder cb, Expression<String> attribute, final String fieldValue) {
		return cb.like(cb.lower(attribute), "%" + fieldValue.toLowerCase(Locale.getDefault()) + "%");
	}
}
