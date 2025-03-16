package com.nn_group.currencyaccount.domain.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Account {
    private UUID id;
    private String firstName;
    private String lastName;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal baseAmount = BigDecimal.ZERO;
    private BigDecimal targetAmount = BigDecimal.ZERO;

    public void exchange(Currency fromCurrency, Currency toCurrency, BigDecimal amount, BigDecimal rate) {
        if (baseCurrency.equals(fromCurrency) && targetCurrency.equals(toCurrency)) {
            BigDecimal convertedAmount = amount.multiply(rate);
            if (baseAmount.compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient " + fromCurrency + " balance");
            }
            baseAmount = baseAmount.subtract(amount);
            targetAmount = targetAmount.add(convertedAmount);
        } else if (targetCurrency.equals(fromCurrency) && baseCurrency.equals(toCurrency)) {
            BigDecimal convertedAmount = amount.multiply(rate);
            if (targetAmount.compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient " + fromCurrency + " balance");
            }
            targetAmount = targetAmount.subtract(amount);
            baseAmount = baseAmount.add(convertedAmount);
        } else {
            throw new IllegalArgumentException("Invalid exchange direction for this account");
        }
    }
}