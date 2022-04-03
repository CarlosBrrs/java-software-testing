package com.amigoscode.testing.payment.services.stripe;

import com.amigoscode.testing.payment.util.Currency;
import com.amigoscode.testing.payment.util.paymentcharger.IPaymentCharger;
import com.amigoscode.testing.payment.util.paymentcharger.PaymentCharge;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
//Spring inicializará este bean cuando la propiedad stripe.enabled tenga el valor false
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "false"
)
public class MockStripeService implements IPaymentCharger {


    @Override
    public PaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description) {
        return new PaymentCharge(true);
    }
}
