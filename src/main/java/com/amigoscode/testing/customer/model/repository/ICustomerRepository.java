package com.amigoscode.testing.customer.model.repository;

import com.amigoscode.testing.customer.model.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ICustomerRepository extends CrudRepository<Customer, UUID> {
}
