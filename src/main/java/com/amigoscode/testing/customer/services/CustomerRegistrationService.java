package com.amigoscode.testing.customer.services;

import com.amigoscode.testing.customer.model.repository.ICustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerRegistrationService implements ICustomerRegistrationService{

    private final ICustomerRepository customerRepository;

    @Autowired
    public CustomerRegistrationService(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
}
