package com.nn_group.currencyaccount.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(UUID.randomUUID());
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setBaseCurrency(Currency.PLN);
        account.setTargetCurrency(Currency.USD);
        account.setBaseAmount(BigDecimal.valueOf(1000));
        account.setTargetAmount(BigDecimal.valueOf(0));
    }

    @Test
    void shouldExchangePlnToUsdSuccessfully() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal rate = BigDecimal.valueOf(0.2597);

        // when
        account.exchange(Currency.PLN, Currency.USD, amount, rate);

        // then
        assertThat(account.getBaseAmount()).isEqualByComparingTo(BigDecimal.valueOf(900));
        assertThat(account.getTargetAmount()).isEqualByComparingTo(BigDecimal.valueOf(25.97));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBaseCurrency() {
        // given
        BigDecimal amount = BigDecimal.valueOf(1100);
        BigDecimal rate = BigDecimal.valueOf(0.2597);

        // when // then
        assertThatThrownBy(() -> account.exchange(Currency.PLN, Currency.USD, amount, rate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient PLN balance");
    }

    @Test
    void shouldThrowExceptionWhenInsufficientTargetCurrency() {
        // given
        account.setTargetAmount(BigDecimal.valueOf(25.97));
        BigDecimal amount = BigDecimal.valueOf(30);
        BigDecimal rate = BigDecimal.valueOf(3.85);

        // when // then
        assertThatThrownBy(() -> account.exchange(Currency.USD, Currency.PLN, amount, rate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient USD balance");
    }

    @Test
    void shouldThrowExceptionForInvalidExchangeDirection() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal rate = BigDecimal.valueOf(0.2597);

        // when // then
        assertThatThrownBy(() -> account.exchange(Currency.EUR, Currency.USD, amount, rate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid exchange direction for this account");
    }
}