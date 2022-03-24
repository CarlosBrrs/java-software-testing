package com.amigoscode.testing.payment.services;

import com.amigoscode.testing.payment.util.Currency;
import com.amigoscode.testing.payment.util.paymentcharger.IPaymentCharger;
import com.amigoscode.testing.payment.util.paymentcharger.PaymentCharge;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService implements IPaymentCharger {

    private final StripeApi stripeApi;

    private final static RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();

    @Autowired
    public StripeService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    @Override
    public PaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description) {

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("source", cardSource);
        params.put("description", description);

        try {
            //Esta linea conecta a la API real de Stripe
            //Los métodos estáticos no se pueden mockear, se creará una clase que si se pueda mockear y use este método create
            Charge charge = stripeApi.create(params, requestOptions);
            return new PaymentCharge(charge.getPaid());
        } catch (StripeException e) {
            throw new IllegalStateException("Cannot make stripe charge", e);
        }
    }
}
