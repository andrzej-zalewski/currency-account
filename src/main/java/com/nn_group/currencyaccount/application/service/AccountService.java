package com.nn_group.currencyaccount.application.service;

import com.nn_group.currencyaccount.domain.model.Account;
import com.nn_group.currencyaccount.domain.model.Currency;
import com.nn_group.currencyaccount.domain.port.AccountRepositoryPort;
import com.nn_group.currencyaccount.domain.port.ExchangeRatePort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class AccountService {
    private final AccountRepositoryPort accountRepositoryPort;
    private final ExchangeRatePort exchangeRatePort;
    private final Currency defaultBaseCurrency;

    public Account createAccount(String firstName, String lastName, Currency targetCurrency, BigDecimal initialBaseAmount) {
        Account account = new Account();
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setBaseCurrency(defaultBaseCurrency);
        account.setTargetCurrency(targetCurrency);
        account.setBaseAmount(initialBaseAmount);
        return accountRepositoryPort.save(account);
    }

    public Account getAccount(UUID id) {
        return accountRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account with ID " + id + " not found"));
    }

    public Account exchangeCurrency(UUID id, Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        Account account = getAccount(id);

        if (!account.getBaseCurrency().equals(fromCurrency) && !account.getTargetCurrency().equals(fromCurrency)) {
            throw new IllegalArgumentException("Source currency " + fromCurrency + " not supported by this account");
        }
        if (!account.getBaseCurrency().equals(toCurrency) && !account.getTargetCurrency().equals(toCurrency)) {
            throw new IllegalArgumentException("Target currency " + toCurrency + " not supported by this account");
        }

        BigDecimal rate;
        if (fromCurrency.equals(defaultBaseCurrency)) {
            rate = exchangeRatePort.getExchangeRate(toCurrency);
        } else if (toCurrency.equals(defaultBaseCurrency)) {
            rate = BigDecimal.ONE.divide(exchangeRatePort.getExchangeRate(fromCurrency), 4, BigDecimal.ROUND_HALF_UP);
        } else {
            throw new IllegalArgumentException("Exchange between " + fromCurrency + " and " + toCurrency + " not supported yet");
        }

        account.exchange(fromCurrency, toCurrency, amount, rate);
        return accountRepositoryPort.save(account);
    }
}