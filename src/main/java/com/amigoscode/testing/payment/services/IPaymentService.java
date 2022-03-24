package com.amigoscode.testing.payment.services;

import com.amigoscode.testing.payment.domain.model.PaymentRequest;

import java.util.UUID;

public interface IPaymentService {

    void serviceToChargeCard(UUID customerId, PaymentRequest paymentRequest);
}
