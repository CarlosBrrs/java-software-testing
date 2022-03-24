package com.amigoscode.testing.payment.domain.repository;

import com.amigoscode.testing.payment.domain.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.amigoscode.testing.payment.util.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(       //Para probar contra una H2 se necesita esta anotación
        properties = {"spring.jpa.properties.javax.persistence.validation.mode=none"} //Para que las anotaciones de la identidad tambien se validen en los test, y no solo en el run
)
class IPaymentRepositoryTest {

    @Autowired
    private IPaymentRepository underTest;

    @Test
    void itShouldInsertPayment() {
        //Given
        UUID customerId = UUID.randomUUID();
        Long id = 1L; //Al ser generated value, el lo guarda con 1 y por eso existirá en BD, con otro numero no funciona porque solo se ejecuta .save una vez
        Payment payment = new Payment(id, customerId, new BigDecimal("12.345"), USD, "198765", "MasterCard");

        //When
        underTest.save(payment);

        //Then
        Optional<Payment> optionalPayment = underTest.findById(id);
        assertThat(optionalPayment)
                .isPresent().hasValueSatisfying(p -> {
                    assertThat(p).isEqualToComparingFieldByField(payment);
                });

    }
}