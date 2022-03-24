package com.amigoscode.testing.payment.util.paymentcharger;

import com.amigoscode.testing.payment.util.Currency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

public interface IPaymentCharger {

    PaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description);
}
