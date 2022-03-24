package com.amigoscode.testing.customer.services;

import com.amigoscode.testing.customer.domain.model.Customer;
import com.amigoscode.testing.customer.domain.model.CustomerRegistrationRequest;
import com.amigoscode.testing.customer.domain.repository.ICustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService implements ICustomerRegistrationService {

    private final ICustomerRepository customerRepository;

    @Autowired
    public CustomerRegistrationService(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void serviceToRegisterNewCustomer(CustomerRegistrationRequest request) {
        /*
         * 1. Validate if phone number is taken
         * 2. If it is taken, then validate if belongs to same customer
         * - 2.1. If not throw an exception
         * - 2.2. If yes, return
         * 3. if is not taken, save customer
         */

        String phoneNumber = request.getCustomer().getPhoneNumber();

        Optional<Customer> optionalCustomer = customerRepository.selectCustomerByPhoneNumber(
                phoneNumber);

        if (optionalCustomer.isPresent()) { //Si el cliente existe en BD, comprueba si no es, o es el mismo que te paso yo

            if (!optionalCustomer.get().getName().equals(request.getCustomer().getName()))
                throw new IllegalStateException(          //No es el mismo cliente?
                        "Phone number: " + optionalCustomer.get().getPhoneNumber() + " is already taken by" + optionalCustomer.get().getName());
            else return; // Es el mismo cliente

        }

        if (request.getCustomer().getId() == null)
            request.getCustomer().setId(UUID.randomUUID()); //Asignar del lado del back el id cuando viene null de la petición

        customerRepository.save(request.getCustomer()); //Si el cliente no existe en BD, entonces guardarlo (el que viene en el request)
    }
}

