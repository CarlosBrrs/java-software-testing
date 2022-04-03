package com.amigoscode.testing.payment.services;

import com.amigoscode.testing.customer.domain.model.Customer;
import com.amigoscode.testing.customer.domain.model.CustomerRegistrationRequest;
import com.amigoscode.testing.payment.domain.model.Payment;
import com.amigoscode.testing.payment.domain.model.PaymentRequest;
import com.amigoscode.testing.payment.domain.repository.IPaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static com.amigoscode.testing.payment.util.Currency.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest //Cualquier test que se ejecute en una clase con esta anotación, levanta toda la aplicación primero
@AutoConfigureMockMvc //Para testear url´s o controllers
class PaymentIntegrationTest {

    @Autowired
    private IPaymentRepository paymentRepository; //No es correcto, se deberia solo tener mockMvc y un endpoint que entregue los payment por id

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        //Given a customer
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "James", "0000000");

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        //Register
        ResultActions customerRegResultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(customerRegistrationRequest))));

        // ... payment
        long paymentId = 1L;
        Payment payment = new Payment(paymentId, customerId, new BigDecimal("12.12"), USD, "mySource", "donation");

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        //When payment is sent
        ResultActions paymentResultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        //Then both customer registration and payment requests are 200 status code
        customerRegResultActions.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());

        //Payment is stored in DB
        //TODO: Do not use paymentRepository instead create an endpoint to retrieve payment for customers
        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByField(payment));

        //TODO: Ensure sms is delivered
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to Json");
            return null;
        }
    }
}
