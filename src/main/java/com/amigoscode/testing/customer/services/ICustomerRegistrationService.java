package com.amigoscode.testing.customer.services;


import com.amigoscode.testing.customer.domain.model.CustomerRegistrationRequest;

public interface ICustomerRegistrationService {

    void serviceToRegisterNewCustomer(CustomerRegistrationRequest request);

}
