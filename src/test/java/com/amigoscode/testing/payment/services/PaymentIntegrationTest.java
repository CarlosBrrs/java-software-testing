package com.amigoscode.testing.payment.services;

import com.amigoscode.testing.customer.domain.model.Customer;
import com.amigoscode.testing.customer.domain.model.CustomerRegistrationRequest;
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

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest //Cualquier test que se ejecute en una clase con esta anotación, levanta toda la aplicación primero
@AutoConfigureMockMvc //Para testear url´s o controllers
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        //Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "James", "0000000");

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        ResultActions customerRegResultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(request))));

        //System.out.println(customerRegResultActions);
        //When

        //Then
        customerRegResultActions.andExpect(status().isOk());
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
