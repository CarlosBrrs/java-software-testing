package com.amigoscode.testing.payment.util.paymentcharger;

public class PaymentCharge {

    private final boolean wasCardCharged;

    public PaymentCharge(boolean wasCardCharged) {
        this.wasCardCharged = wasCardCharged;
    }

    public boolean WasCardCharged() {
        return wasCardCharged;
    }

    @Override
    public String toString() {
        return "PaymentCharge{" +
                "wasCardCharged=" + wasCardCharged +
                '}';
    }
}
