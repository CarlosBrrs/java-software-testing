package com.amigoscode.testing.payment.services;

import com.amigoscode.testing.customer.domain.model.Customer;
import com.amigoscode.testing.customer.domain.repository.ICustomerRepository;
import com.amigoscode.testing.payment.domain.model.Payment;
import com.amigoscode.testing.payment.domain.model.PaymentRequest;
import com.amigoscode.testing.payment.domain.repository.IPaymentRepository;
import com.amigoscode.testing.payment.util.Currency;
import com.amigoscode.testing.payment.util.paymentcharger.IPaymentCharger;
import com.amigoscode.testing.payment.util.paymentcharger.PaymentCharge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.amigoscode.testing.payment.util.Currency.COP;
import static com.amigoscode.testing.payment.util.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    private IPaymentService underTest;
    @Mock
    private ICustomerRepository customerRepository;
    @Mock
    private IPaymentRepository paymentRepository;
    @Mock
    private IPaymentCharger paymentCharger;
    @Captor
    ArgumentCaptor<Payment> argumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, paymentCharger);
    }

    @Test
    void itShouldChargeNewPaymentSuccessfully() {
        //Given
        UUID customerId = UUID.randomUUID();

        // ... customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //Construct payment request
        Currency currency = USD;
        Payment payment = new Payment(null, null, BigDecimal.valueOf(12.12), currency, "123345", "Donation");
        PaymentRequest request = new PaymentRequest(payment);

        // ... card is charged successfully
        PaymentCharge paymentCharge = new PaymentCharge(true);
        when(paymentCharger.chargeCard(payment.getSource(), payment.getAmount(), payment.getCurrency(), payment.getDescription()))
                .thenReturn(paymentCharge);
        //When
        underTest.serviceToChargeCard(customerId, request);

        //Then
        then(paymentRepository).should().save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualToComparingFieldByField(payment);

    }

    @Test
    void itShouldThrowCardNotCharged() {
        //Given
        UUID customerId = UUID.randomUUID();

        // ... customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //Construct payment request
        Currency currency = USD;
        Payment payment = new Payment(null, null, BigDecimal.valueOf(12.12), currency, "123345", "Donation");
        PaymentRequest request = new PaymentRequest(payment);

        // ... card is not charged successfully
        PaymentCharge paymentCharge = new PaymentCharge(false);
        when(paymentCharger.chargeCard(payment.getSource(), payment.getAmount(), payment.getCurrency(), payment.getDescription()))
                .thenReturn(paymentCharge);
        //When
        assertThatThrownBy(() -> underTest.serviceToChargeCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Card not debited for costumer " + customerId);

        //Then
        then(paymentRepository).should(never()).save(any(Payment.class));
        then(paymentRepository).shouldHaveNoInteractions();

    }

    @Test
    void itShouldThrowCurrencyNotSupported() {
        //Given
        UUID customerId = UUID.randomUUID();

        // ... customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        // ... Currency not supported in List in PaymentService
        Currency currency = COP;

        //Construct payment request
        Payment payment = new Payment(null, null, BigDecimal.valueOf(12.12), currency, "123345", "Donation");
        PaymentRequest request = new PaymentRequest(payment);

        //When
        assertThatThrownBy(() -> underTest.serviceToChargeCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("We cannot process the transaction in " + payment.getCurrency());

        //Then
        then(paymentRepository).should(never()).save(any(Payment.class));
        then(paymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();

    }

    @Test
    void itShouldThrowCustomerDoesNotExist() {
        //Given
        UUID customerId = UUID.randomUUID();

        // ... customer does not exist
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        //Construct payment request
        Currency currency = USD;
        Payment payment = new Payment(null, null, BigDecimal.valueOf(12.12), currency, "123345", "Donation");
        PaymentRequest request = new PaymentRequest(payment);

        //When
        //Then
        assertThatThrownBy(() -> underTest.serviceToChargeCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Customer with ID: " + customerId + " does not exists ir our database");

        //Then
        then(paymentRepository).should(never()).save(any(Payment.class));
        then(paymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();

    }
}