package com.amigoscode.testing.customer.domain.repository;

import com.amigoscode.testing.customer.domain.model.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICustomerRepository extends CrudRepository<Customer, UUID> {

    @Query(
            value = "SELECT id, name, phone_number " +
                    "FROM customer " +
                    "WHERE phone_number = :phone_number",
            nativeQuery = true                              //To use native SQL statements instead of Entities names like Customer
    )
    Optional<Customer> selectCustomerByPhoneNumber(@Param("phone_number") String phoneNumber);

}
