package com.amigoscode.testing.payment.services;

import com.amigoscode.testing.customer.domain.model.Customer;
import com.amigoscode.testing.customer.domain.repository.ICustomerRepository;
import com.amigoscode.testing.payment.domain.model.Payment;
import com.amigoscode.testing.payment.domain.model.PaymentRequest;
import com.amigoscode.testing.payment.domain.repository.IPaymentRepository;
import com.amigoscode.testing.payment.util.Currency;
import com.amigoscode.testing.payment.util.paymentcharger.IPaymentCharger;
import com.amigoscode.testing.payment.util.paymentcharger.PaymentCharge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.amigoscode.testing.payment.util.Currency.EUR;
import static com.amigoscode.testing.payment.util.Currency.GBP;
import static com.amigoscode.testing.payment.util.Currency.USD;

@Service
public class PaymentService implements IPaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(EUR, GBP, USD);

    private final ICustomerRepository customerRepository;
    private final IPaymentRepository paymentRepository;
    private final IPaymentCharger paymentCharger; //Se usa la interface para mockear el servicio de Stripe, que cargará la tarjeta. Cualquiera que quiera implementar esta interface debe seguir sus firmas

    @Autowired
    public PaymentService(ICustomerRepository customerRepository, IPaymentRepository paymentRepository, IPaymentCharger paymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.paymentCharger = paymentCharger;
    }

    @Override
    public void serviceToChargeCard(UUID customerId, PaymentRequest paymentRequest) {
        /*
         * 1. Does customer exists If not throw
         * 2. Can we support the currency if not throw
         * 1.2. else charge card
         * 1.2.1. If not debited throw
         * 1.2.2. else insert payment
         * TODO: send sms*/

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isEmpty()) {
            throw new IllegalStateException("Customer with ID: " + customerId + " does not exists ir our database");
        }

        Payment payment = paymentRequest.getPayment();
        boolean isCurrencyAccepted = ACCEPTED_CURRENCIES.stream()
                .anyMatch(currency -> payment.getCurrency().equals(currency));

        if (!isCurrencyAccepted) {
            throw new IllegalStateException("We cannot process the transaction in " + payment.getCurrency());
        }

        PaymentCharge paymentCharge = paymentCharger.chargeCard(payment.getSource(), payment.getAmount(), payment.getCurrency(), payment.getDescription());

        if (!paymentCharge.WasCardCharged()) {
            throw new IllegalStateException("Card not debited for costumer " + customerId);
        }

        payment.setCustomerId(customerId);
        paymentRepository.save(payment);

    }
}
