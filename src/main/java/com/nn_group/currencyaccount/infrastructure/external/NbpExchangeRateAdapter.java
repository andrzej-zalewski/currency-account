package com.nn_group.currencyaccount.infrastructure.external;

import com.nn_group.currencyaccount.domain.model.Currency;
import com.nn_group.currencyaccount.domain.port.ExchangeRatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class NbpExchangeRateAdapter implements ExchangeRatePort {
    private final NbpApiClient nbpApiClient;

    @Value("${currency.default-base}")
    private String defaultBaseCurrency;

    @Override
    public BigDecimal getExchangeRate(Currency targetCurrency) {
        Currency defaultCurrency = Currency.valueOf(defaultBaseCurrency);
        if (targetCurrency == defaultCurrency) {
            return BigDecimal.ONE;
        }
        NbpExchangeRateResponse response = nbpApiClient.getExchangeRate(targetCurrency.name());
        if (response == null || response.getRates() == null || response.getRates().isEmpty()) {
            throw new RuntimeException("Invalid or empty response from NBP API for " + targetCurrency);
        }
        return response.getRates().get(0).getMid();
    }
}