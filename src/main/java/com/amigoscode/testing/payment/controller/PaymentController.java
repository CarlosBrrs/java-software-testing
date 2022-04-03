package com.amigoscode.testing.payment.controller;

import com.amigoscode.testing.payment.domain.model.PaymentRequest;
import com.amigoscode.testing.payment.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping()
    public void makePayment(@RequestBody PaymentRequest request) {
        paymentService.serviceToChargeCard(request.getPayment().getCustomerId(), request);
    }

}
