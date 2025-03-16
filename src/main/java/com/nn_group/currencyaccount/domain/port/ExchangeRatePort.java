package com.nn_group.currencyaccount.domain.port;

import com.nn_group.currencyaccount.domain.model.Currency;

import java.math.BigDecimal;

public interface ExchangeRatePort {
    BigDecimal getExchangeRate(Currency targetCurrency);
}