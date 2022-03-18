package com.amigoscode.testing.customer.model.repository;

import com.amigoscode.testing.customer.model.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest        //Para probar contra una H2 se necesita esta anotación
class ICustomerRepositoryTest {

    @Autowired
    private ICustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        //Given

        //When

        //Then

    }

    @Test
    void itShouldSaveCustomer() {
        //Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Harry", "0000");

        //When
        underTest.save(customer);

        //Then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer)
                .isPresent().hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualTo(id);
//                    assertThat(c.getName()).isEqualTo("Harry");
//                    assertThat(c.getPhoneNumber()).isEqualTo("0000");
                    assertThat(c).isEqualToComparingFieldByField(customer);
                }
        );

    }
}