package com.amigoscode.testing.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }

    //Se condensan los 3 test de abajo en uno solo parametrizado
    @ParameterizedTest
    @CsvSource({
            "+573000000000, true",
            "+573000000000444, false",
            "573000000000, false"

    })
    void itShouldValidatePhoneNumber(String phoneNumber, boolean expected) {
        //Given

        //When
        boolean isPhoneNumberValid = underTest.test(phoneNumber);

        //Then
        assertThat(isPhoneNumberValid).isEqualTo(expected);

    }
    /* Con el test parametrizado, ya no se necesitan estos
    @Test
    void itShouldValidatePhoneNumber() {
        //Given
        String phoneNumber = "+573000000000";

        //When
        boolean isPhoneNumberValid = underTest.test(phoneNumber);

        //Then
        assertThat(isPhoneNumberValid).isTrue();

    }

    @Test
    @DisplayName("Should fail when length is bigger than 13")
    void itShouldValidatePhoneNumberWhenIncorrectAndHasLengthBiggerThan13() {
        //Given
        String phoneNumber = "+573000000000444";

        //When
        boolean isPhoneNumberValid = underTest.test(phoneNumber);

        //Then
        assertThat(isPhoneNumberValid).isFalse();

    }

    @Test
    @DisplayName("Should fail when does not start with +")
    void itShouldValidatePhoneNumberWhenIncorrectAndDoesNotStartWithPlusSign() {
        //Given
        String phoneNumber = "573000000000";

        //When
        boolean isPhoneNumberValid = underTest.test(phoneNumber);

        //Then
        assertThat(isPhoneNumberValid).isFalse();

    }*/
}
