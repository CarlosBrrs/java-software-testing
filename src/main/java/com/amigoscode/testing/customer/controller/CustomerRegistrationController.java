package com.amigoscode.testing.customer.controller;

import com.amigoscode.testing.customer.model.domain.CustomerRegistrationRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/customer-registration")
public class CustomerRegistrationController {

    @PutMapping
    public void registerNewCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {

    }
}
