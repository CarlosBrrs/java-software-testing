package com.amigoscode.testing.customer.services;

import com.amigoscode.testing.customer.domain.model.Customer;
import com.amigoscode.testing.customer.domain.model.CustomerRegistrationRequest;
import com.amigoscode.testing.customer.domain.repository.ICustomerRepository;
import com.amigoscode.testing.utils.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    private ICustomerRegistrationService underTest;

    @Mock
    private ICustomerRepository customerRepository; //Se mockea porque previamente debe estar completamente testeada y no se quiere testear de nuevo acá
    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository, phoneNumberValidator);
    }

    @Test
    void itShouldSaveNewCustomer() {
        //Given a phone number, id and customer
        String phoneNumber = "000099"; //It is not used because we are mocking de response of the validator
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Ronald", phoneNumber);

        // ... a request (parameter of method under test)
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... No customer with phone number founded
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // ... Phone number is valid
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        underTest.serviceToRegisterNewCustomer(request);

        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture()); //Esto deberia pasar al ejecutar el método bajo prueba, y voy a capturar lo que deberia recibir el .save para poder realizar el assert con él
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        //Given a phone number, id and customer
        String phoneNumber = "000099"; //It is not used because we are mocking de response of the validator
        Customer customer = new Customer(null, "Ronald", phoneNumber);

        // ... a request (parameter of method under test)
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... No customer with phone number founded
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // ... Phone number is valid
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        underTest.serviceToRegisterNewCustomer(request);

        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture()); //Esto deberia pasar al ejecutar el método bajo prueba, y voy a capturar lo que deberia recibir el .save para poder realizar el assert con él
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveNewCustomerWhenCustomerExists() {
        //Given
        String phoneNumber = "000099"; //It is not used because we are mocking de response of the validator
        UUID id = UUID.randomUUID();
        Customer customerRequest = new Customer(id, "Ronald", phoneNumber);

        // ... a request (parameter of method under test)
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customerRequest);

        // ... An existing customer is returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customerRequest));

        // ... Phone number is valid
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        underTest.serviceToRegisterNewCustomer(request);

        //Then
        then(customerRepository).should(never()).save(any()); //Método save nunca deberia ejecutar .save con cualquier parámetro

//        //Camino alternativo: customerRepository deberia ejecutar el select y luego no realizar más acciones
//        then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
//        then(customerRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    void itShouldThrowNewIllegalStateException() {
        //Given a phone number, id and customer
        String phoneNumber = "000099"; //It is not used because we are mocking de response of the validator
        UUID id = UUID.randomUUID();
        Customer requestCustomer = new Customer(id, "Ronald", phoneNumber);
        Customer optionalCustomer = new Customer(id, "Hermione", phoneNumber);


        // ... a request (parameter of method under test)
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(requestCustomer);

        // ... No customer with phone number founded
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(optionalCustomer));

        // ... Phone number is valid
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        assertThatThrownBy(() -> underTest.serviceToRegisterNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Phone number: " + optionalCustomer.getPhoneNumber() + " is already taken by" + optionalCustomer.getName());

        //Then
        then(customerRepository).should(never()).save(any()); //Método save nunca deberia ejecutar .save con cualquier parámetro

    }

    @Test
    void itShouldThrowNewIllegalStateExceptionWhenPhoneNumberIsNotValid() {
        //Given a phone number, id and customer
        String phoneNumber = "000099"; //It is not used because we are mocking de response of the validator
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Ronald", phoneNumber);

        // ... a request (parameter of method under test)
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... Phone number is valid
        given(phoneNumberValidator.test(phoneNumber)).willReturn(false);

        //When
        assertThatThrownBy(() -> underTest.serviceToRegisterNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Phone number " + phoneNumber + " is not colombian/valid");


        //Then
        then(customerRepository).should(never()).save(any()); //Esto deberia pasar al ejecutar el método bajo prueba, y voy a capturar lo que deberia recibir el .save para poder realizar el assert con él
    }
}