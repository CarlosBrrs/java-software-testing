package com.amigoscode.testing.payment.services;

import com.amigoscode.testing.payment.util.Currency;
import com.amigoscode.testing.payment.util.paymentcharger.IPaymentCharger;
import com.amigoscode.testing.payment.util.paymentcharger.PaymentCharge;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static com.amigoscode.testing.payment.util.Currency.EUR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class StripeServiceTest {

    private IPaymentCharger underTest;

    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new StripeService(stripeApi);
    }

    @Test
    void itShouldChargeCard() throws StripeException {
        //Given
        String cardSource = "MasterCard123";
        BigDecimal amount = new BigDecimal("12.12");
        Currency currency = EUR;
        String description = "Salary";

        // No esta yendo realmente a la API, hay que setearle el resultado como true para que pase
        // Successful charge
        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(), any())).willReturn(charge);

        //When
        PaymentCharge paymentCharge = underTest.chargeCard(cardSource, amount, currency, description);

        //Then
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> requestOptionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);

        //Capturo lo que se le pasa a create para poder compararlos con los Given
        // Captor requestMap and options
        then(stripeApi).should().create(mapArgumentCaptor.capture(), requestOptionsArgumentCaptor.capture());

        // Assert on requestMap
        Map<String, Object> mapRequest = mapArgumentCaptor.getValue();
        assertThat(mapRequest.keySet()).hasSize(4);
        assertThat(mapRequest.get("source")).isEqualTo(cardSource);
        assertThat(mapRequest.get("amount")).isEqualTo(amount);
        assertThat(mapRequest.get("currency")).isEqualTo(currency);
        assertThat(mapRequest.get("description")).isEqualTo(description);

        // Assert on options
        RequestOptions optionsRequest = requestOptionsArgumentCaptor.getValue();
        assertThat(optionsRequest).isNotNull();

        // card is debited successfully
        assertThat(paymentCharge).isNotNull();
        assertThat(paymentCharge.WasCardCharged()).isTrue();

    }

    @Test
    void itShouldThrowCannotMakeCharge() throws StripeException {
        //Given
        String cardSource = "MasterCard123";
        BigDecimal amount = new BigDecimal("12.12");
        Currency currency = EUR;
        String description = "Salary";

        // Throw exception when stripe api is called
        StripeException stripeException = mock(StripeException.class);
        doThrow(stripeException).when(stripeApi).create(anyMap(), any());

        //When

        //Then
        assertThatThrownBy(() -> underTest.chargeCard(cardSource, amount, currency, description))
                .isInstanceOf(IllegalStateException.class)
                .hasRootCause(stripeException)
                .hasMessageContaining("Cannot make stripe charge");

    }
}