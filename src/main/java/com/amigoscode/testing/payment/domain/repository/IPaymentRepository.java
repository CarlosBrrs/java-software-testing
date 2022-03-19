package com.amigoscode.testing.payment.domain.repository;

import com.amigoscode.testing.payment.domain.model.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaymentRepository extends CrudRepository<Payment, Long> {
}
